package com.lakshan.medical_records_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class TestResultDtos {

    public record CreateRequest(
            @NotNull Long labTestId,
            @NotNull Long patientId,
            @NotNull Long technicianId,
            @NotBlank String testResultDescription,
            String technicianNotes
    ) {}

    public record Response(
            Long id,
            Long labTestId,
            Long patientId,
            Long technicianId,
            String testResultDescription,
            String technicianNotes,
            String filePath,
            String fileName,
            String fileType,
            Long fileSize,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record PatientViewResponse(
            Long id,
            Long labTestId,
            String testName,
            String testResultDescription,
            String technicianNotes,
            String fileName,
            String fileType,
            Long fileSize,
            LocalDateTime createdAt,
            String technicianName
    ) {}
}
