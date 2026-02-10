package com.lakshan.consultation_service.service;

import com.lakshan.consultation_service.client.UserClient;
import com.lakshan.consultation_service.domain.Consultation;
import com.lakshan.consultation_service.domain.LabTest;
import com.lakshan.consultation_service.domain.LabTest.TestStatus;
import com.lakshan.consultation_service.dto.LabTestDtos;
import com.lakshan.consultation_service.mapper.LabTestMapper;
import com.lakshan.consultation_service.repository.ConsultationRepository;
import com.lakshan.consultation_service.repository.LabTestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabTestService {
    
    private final LabTestRepository labTestRepository;
    private final ConsultationRepository consultationRepository;
    private final UserClient userClient;

    public LabTestService(LabTestRepository labTestRepository, 
                         ConsultationRepository consultationRepository,
                         UserClient userClient,
                         ConsultationService consultationService) {
        this.labTestRepository = labTestRepository;
        this.consultationRepository = consultationRepository;
        this.userClient = userClient;
        // Set circular dependency to avoid initialization issues
        consultationService.setLabTestService(this);
    }

    @Transactional
    public LabTestDtos.Response createTest(LabTestDtos.CreateRequest req) {
        // Validate consultation exists
        Consultation consultation = consultationRepository.findById(req.consultationId())
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
        
        LabTest labTest = LabTestMapper.toEntity(req);
        labTest.setStatus(TestStatus.PENDING);
        LabTest saved = labTestRepository.save(labTest);
        return LabTestMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LabTestDtos.Response getTest(Long id) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lab test not found"));
        return LabTestMapper.toResponse(labTest);
    }

    @Transactional(readOnly = true)
    public List<LabTestDtos.Response> getTestsByConsultation(Long consultationId) {
        return labTestRepository.findByConsultationId(consultationId)
                .stream()
                .map(LabTestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<LabTestDtos.Response> listTests(TestStatus status, Long technicianId, 
                                                 LocalDateTime start, LocalDateTime end, 
                                                 Pageable pageable) {
        if (status != null && technicianId != null) {
            return labTestRepository.findByStatusAndTechnicianId(status, technicianId, pageable)
                    .map(LabTestMapper::toResponse);
        }
        if (status != null) {
            return labTestRepository.findByStatus(status, pageable)
                    .map(LabTestMapper::toResponse);
        }
        if (technicianId != null) {
            return labTestRepository.findByAssignedTechnicianId(technicianId, pageable)
                    .map(LabTestMapper::toResponse);
        }
        if (start != null && end != null) {
            return labTestRepository.findByCreatedAtBetween(start, end, pageable)
                    .map(LabTestMapper::toResponse);
        }
        return labTestRepository.findAll(pageable)
                .map(LabTestMapper::toResponse);
    }

    @Transactional
    public LabTestDtos.Response updateTest(Long id, LabTestDtos.UpdateRequest req) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lab test not found"));
        
        if (req.testDescription() != null) labTest.setTestDescription(req.testDescription());
        if (req.testInstructions() != null) labTest.setTestInstructions(req.testInstructions());
        if (req.assignedTechnicianId() != null) {
            // Optionally validate technician exists
            labTest.setAssignedTechnicianId(req.assignedTechnicianId());
        }
        if (req.testResults() != null) labTest.setTestResults(req.testResults());
        if (req.technicianNotes() != null) labTest.setTechnicianNotes(req.technicianNotes());
        if (req.status() != null) {
            applyStatusTransition(labTest, req.status());
        }
        
        LabTest saved = labTestRepository.save(labTest);
        return LabTestMapper.toResponse(saved);
    }

    @Transactional
    public LabTestDtos.Response updateTestByTechnician(Long id, LabTestDtos.TechnicianUpdateRequest req) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lab test not found"));
        
        if (req.testResults() != null) labTest.setTestResults(req.testResults());
        if (req.technicianNotes() != null) labTest.setTechnicianNotes(req.technicianNotes());
        if (req.status() != null) {
            applyStatusTransition(labTest, req.status());
        }
        
        LabTest saved = labTestRepository.save(labTest);
        return LabTestMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTest(Long id) {
        if (!labTestRepository.existsById(id)) {
            throw new IllegalArgumentException("Lab test not found");
        }
        labTestRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<LabTestDtos.TechnicianDashboardResponse> getTechnicianDashboard(
            Long technicianId, TestStatus status, Pageable pageable) {
        
        Page<LabTest> tests;
        if (technicianId != null && status != null) {
            tests = labTestRepository.findByStatusAndTechnicianId(status, technicianId, pageable);
        } else if (technicianId != null) {
            tests = labTestRepository.findByAssignedTechnicianId(technicianId, pageable);
        } else if (status != null) {
            tests = labTestRepository.findByStatus(status, pageable);
        } else {
            tests = labTestRepository.findAll(pageable);
        }
        
        return tests.map(this::toDashboardResponse);
    }

    private LabTestDtos.TechnicianDashboardResponse toDashboardResponse(LabTest labTest) {
        Consultation consultation = consultationRepository.findById(labTest.getConsultationId())
                .orElse(null);
        
        LabTestDtos.PatientInfo patientInfo = null;
        LabTestDtos.DoctorInfo doctorInfo = null;
        
        if (consultation != null) {
            // In a real implementation, fetch actual patient/doctor names from UserClient
            patientInfo = new LabTestDtos.PatientInfo(
                    consultation.getPatientId(), 
                    "Patient-" + consultation.getPatientId()
            );
            doctorInfo = new LabTestDtos.DoctorInfo(
                    consultation.getDoctorId(), 
                    "Dr. " + consultation.getDoctorId()
            );
        }
        
        return new LabTestDtos.TechnicianDashboardResponse(
                labTest.getId(),
                labTest.getConsultationId(),
                labTest.getTestName(),
                labTest.getTestDescription(),
                labTest.getTestInstructions(),
                labTest.getStatus(),
                labTest.getTestResults(),
                labTest.getTechnicianNotes(),
                labTest.getCreatedAt(),
                labTest.getUpdatedAt(),
                patientInfo,
                doctorInfo
        );
    }

    private void applyStatusTransition(LabTest labTest, TestStatus newStatus) {
        TestStatus current = labTest.getStatus();
        if (newStatus == TestStatus.PENDING) {
            if (current != TestStatus.PENDING && current != TestStatus.CANCELLED) {
                throw new IllegalStateException("Cannot revert to PENDING from " + current);
            }
        } else if (newStatus == TestStatus.IN_PROGRESS) {
            if (current != TestStatus.PENDING) {
                throw new IllegalStateException("Can start only from PENDING");
            }
        } else if (newStatus == TestStatus.COMPLETED) {
            if (current != TestStatus.IN_PROGRESS) {
                throw new IllegalStateException("Can complete only from IN_PROGRESS");
            }
            labTest.setCompletedAt(LocalDateTime.now());
        } else if (newStatus == TestStatus.CANCELLED) {
            if (current == TestStatus.COMPLETED) {
                throw new IllegalStateException("Cannot cancel a completed test");
            }
        }
        labTest.setStatus(newStatus);
    }
}
