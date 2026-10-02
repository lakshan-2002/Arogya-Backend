package com.lakshan.consultation_service.dto;

import com.lakshan.consultation_service.domain.LabTest.TestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class LabTestDtos {
    
    public record CreateRequest(
            @NotNull Long consultationId,
            @NotBlank String testName,
            String testDescription,
            String testInstructions
    ) {}

    public record UpdateRequest(
            String testDescription,
            String testInstructions,
            TestStatus status,
            Long assignedTechnicianId,
            String testResults,
            String technicianNotes
    ) {}
    
    public record TechnicianUpdateRequest(
            TestStatus status,
            String testResults,
            String technicianNotes
    ) {}

    public record Response(
            Long id,
            Long consultationId,
            String testName,
            String testDescription,
            String testInstructions,
            TestStatus status,
            Long assignedTechnicianId,
            String testResults,
            String technicianNotes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime completedAt
    ) {}
    
    public record TechnicianDashboardResponse(
            Long id,
            Long consultationId,
            String testName,
            String testDescription,
            String testInstructions,
            TestStatus status,
            String testResults,
            String technicianNotes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            PatientInfo patientInfo,
            DoctorInfo doctorInfo
    ) {}
    
    public record PatientInfo(
            Long patientId,
            String patientName
    ) {}
    
    public record DoctorInfo(
            Long doctorId,
            String doctorName
    ) {}
}
