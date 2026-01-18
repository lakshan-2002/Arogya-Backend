package com.lakshan.consultation_service.mapper;

import com.lakshan.consultation_service.domain.Consultation;
import com.lakshan.consultation_service.dto.ConsultationDtos;

public class ConsultationMapper {
    public static Consultation toEntity(ConsultationDtos.CreateRequest req) {
        Consultation c = new Consultation();
        c.setPatientId(req.patientId());
        c.setDoctorId(req.doctorId());
        c.setClinicId(req.clinicId());
        c.setQueueTokenId(req.queueTokenId());
        c.setChiefComplaint(req.chiefComplaint());
        c.setPastMedicalHistory(req.pastMedicalHistory());
        c.setPresentIllness(req.presentIllness());
        c.setRecommendations(req.recommendations());
        if (req.sessionNumber() != null) c.setSessionNumber(req.sessionNumber());
        if (req.bookedAt() != null) c.setBookedAt(req.bookedAt());
        return c;
    }

    public static ConsultationDtos.Response toResponse(Consultation c) {
        return new ConsultationDtos.Response(
                c.getId(),
                c.getPatientId(),
                c.getDoctorId(),
                c.getClinicId(),
                c.getQueueTokenId(),
                c.getStatus(),
                c.getChiefComplaint(),
                c.getPastMedicalHistory(),
                c.getPresentIllness(),
                c.getRecommendations(),
                c.getSessionNumber(),
                c.getBookedAt(),
                c.getCompletedAt(),
                c.getUpdatedAt()
        );
    }
}
