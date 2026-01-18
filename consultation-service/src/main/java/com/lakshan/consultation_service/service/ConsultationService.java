package com.lakshan.consultation_service.service;

import com.lakshan.consultation_service.client.ClinicClient;
import com.lakshan.consultation_service.client.QueueClient;
import com.lakshan.consultation_service.client.UserClient;
import com.lakshan.consultation_service.domain.Consultation;
import com.lakshan.consultation_service.domain.Consultation.Status;
import com.lakshan.consultation_service.dto.ConsultationDtos;
import com.lakshan.consultation_service.mapper.ConsultationMapper;
import com.lakshan.consultation_service.repository.ConsultationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ConsultationService {
    private final ConsultationRepository repository;
    private final UserClient userClient;
    private final ClinicClient clinicClient;
    private final QueueClient queueClient;

    public ConsultationService(ConsultationRepository repository, UserClient userClient, ClinicClient clinicClient, QueueClient queueClient) {
        this.repository = repository;
        this.userClient = userClient;
        this.clinicClient = clinicClient;
        this.queueClient = queueClient;
    }

    @Transactional
    public ConsultationDtos.Response create(ConsultationDtos.CreateRequest req) {
        // Validate logical references via lightweight existence checks
        if (!userClient.userExists(req.patientId())) throw new IllegalArgumentException("Invalid patientId");
        if (!userClient.doctorExists(req.doctorId())) throw new IllegalArgumentException("Invalid doctorId");
        if (!clinicClient.clinicExists(req.clinicId())) throw new IllegalArgumentException("Invalid clinicId");
        if (req.queueTokenId() != null && !queueClient.queueTokenExists(req.queueTokenId()))
            throw new IllegalArgumentException("Invalid queueTokenId");

        Consultation c = ConsultationMapper.toEntity(req);
        c.setStatus(Status.SCHEDULED);
        c.setBookedAt(c.getBookedAt() != null ? c.getBookedAt() : LocalDateTime.now());
        Consultation saved = repository.save(c);
        return ConsultationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ConsultationDtos.Response get(Long id) {
        Consultation c = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        return ConsultationMapper.toResponse(c);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationDtos.Response> list(Long patientId, Long doctorId, Long clinicId, Status status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if (patientId != null && doctorId != null) {
            return repository.findByPatientIdAndDoctorId(patientId, doctorId, pageable).map(ConsultationMapper::toResponse);
        }
        if (patientId != null) return repository.findByPatientId(patientId, pageable).map(ConsultationMapper::toResponse);
        if (doctorId != null) return repository.findByDoctorId(doctorId, pageable).map(ConsultationMapper::toResponse);
        if (clinicId != null) return repository.findByClinicId(clinicId, pageable).map(ConsultationMapper::toResponse);
        if (status != null) return repository.findByStatus(status, pageable).map(ConsultationMapper::toResponse);
        if (start != null && end != null) return repository.findByBookedAtBetween(start, end, pageable).map(ConsultationMapper::toResponse);
        return repository.findAll(pageable).map(ConsultationMapper::toResponse);
    }

    @Transactional
    public ConsultationDtos.Response update(Long id, ConsultationDtos.UpdateRequest req) {
        Consultation c = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        if (req.chiefComplaint() != null) c.setChiefComplaint(req.chiefComplaint());
        if (req.pastMedicalHistory() != null) c.setPastMedicalHistory(req.pastMedicalHistory());
        if (req.presentIllness() != null) c.setPresentIllness(req.presentIllness());
        if (req.recommendations() != null) c.setRecommendations(req.recommendations());
        if (req.status() != null) applyStatusTransition(c, req.status());
        Consultation saved = repository.save(c);
        return ConsultationMapper.toResponse(saved);
    }

    @Transactional
    public ConsultationDtos.Response cancel(Long id) {
        Consultation c = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        applyStatusTransition(c, Status.CANCELLED);
        return ConsultationMapper.toResponse(repository.save(c));
    }

    @Transactional
    public ConsultationDtos.Response complete(Long id) {
        Consultation c = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        applyStatusTransition(c, Status.COMPLETED);
        c.setCompletedAt(LocalDateTime.now());
        return ConsultationMapper.toResponse(repository.save(c));
    }

    private void applyStatusTransition(Consultation c, Status newStatus) {
        Status current = c.getStatus();
        switch (newStatus) {
            case SCHEDULED -> {
                if (current != Status.SCHEDULED && current != Status.CANCELLED) throw new IllegalStateException("Cannot revert to SCHEDULED from " + current);
            }
            case IN_PROGRESS -> {
                if (current != Status.SCHEDULED) throw new IllegalStateException("Can start only from SCHEDULED");
            }
            case COMPLETED -> {
                if (current != Status.IN_PROGRESS) throw new IllegalStateException("Can complete only from IN_PROGRESS");
            }
            case CANCELLED -> {
                if (current == Status.COMPLETED) throw new IllegalStateException("Cannot cancel a completed consultation");
            }
        }
        c.setStatus(newStatus);
    }
}
