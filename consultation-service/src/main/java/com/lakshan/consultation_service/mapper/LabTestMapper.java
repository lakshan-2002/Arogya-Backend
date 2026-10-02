package com.lakshan.consultation_service.mapper;

import com.lakshan.consultation_service.domain.LabTest;
import com.lakshan.consultation_service.dto.LabTestDtos;

public class LabTestMapper {

    public static LabTest toEntity(LabTestDtos.CreateRequest req) {
        LabTest labTest = new LabTest();
        labTest.setConsultationId(req.consultationId());
        labTest.setTestName(req.testName());
        labTest.setTestDescription(req.testDescription());
        labTest.setTestInstructions(req.testInstructions());
        return labTest;
    }

    public static LabTestDtos.Response toResponse(LabTest labTest) {
        return new LabTestDtos.Response(
                labTest.getId(),
                labTest.getConsultationId(),
                labTest.getTestName(),
                labTest.getTestDescription(),
                labTest.getTestInstructions(),
                labTest.getStatus(),
                labTest.getAssignedTechnicianId(),
                labTest.getTestResults(),
                labTest.getTechnicianNotes(),
                labTest.getCreatedAt(),
                labTest.getUpdatedAt(),
                labTest.getCompletedAt()
        );
    }
}
