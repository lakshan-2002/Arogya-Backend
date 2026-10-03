package com.lakshan.medical_records_service.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TestResultDtos {

    public record CreateRequest(
            Long labTestId,
            Long patientId,
            Long technicianId,
            String testResultDescription,
            String technicianNotes
    ) {}

    public record FileInfo(
            Long id,
            String fileName,
            String fileType,
            Long fileSize,
            LocalDateTime uploadedAt
    ) {}

    public record Response(
            Long id,
            Long labTestId,
            Long patientId,
            Long technicianId,
            String testResultDescription,
            String technicianNotes,
            List<FileInfo> files,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
