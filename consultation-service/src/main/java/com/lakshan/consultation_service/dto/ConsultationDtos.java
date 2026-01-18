package com.lakshan.consultation_service.dto;

import com.lakshan.consultation_service.domain.Consultation.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public class ConsultationDtos {
    public record CreateRequest(
            @NotNull Long patientId,
            @NotNull Long doctorId,
            @NotNull Long clinicId,
            Long queueTokenId,
            String chiefComplaint,
            String pastMedicalHistory,
            String presentIllness,
            String recommendations,
            @Min(1) Integer sessionNumber,
            LocalDateTime bookedAt
    ) {}

    public record UpdateRequest(
            String chiefComplaint,
            String pastMedicalHistory,
            String presentIllness,
            String recommendations,
            Status status
    ) {}

    public record Response(
            Long id,
            Long patientId,
            Long doctorId,
            Long clinicId,
            Long queueTokenId,
            Status status,
            String chiefComplaint,
            String pastMedicalHistory,
            String presentIllness,
            String recommendations,
            Integer sessionNumber,
            LocalDateTime bookedAt,
            LocalDateTime completedAt,
            LocalDateTime updatedAt
    ) {}
}
