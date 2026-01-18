package com.lakshan.consultation_service.repository;

import com.lakshan.consultation_service.domain.Consultation;
import com.lakshan.consultation_service.domain.Consultation.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Page<Consultation> findByPatientId(Long patientId, Pageable pageable);
    Page<Consultation> findByDoctorId(Long doctorId, Pageable pageable);
    Page<Consultation> findByClinicId(Long clinicId, Pageable pageable);
    Page<Consultation> findByStatus(Status status, Pageable pageable);
    Page<Consultation> findByBookedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Consultation> findByPatientIdAndDoctorId(Long patientId, Long doctorId, Pageable pageable);
}
