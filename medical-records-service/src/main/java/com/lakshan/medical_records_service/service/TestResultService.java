package com.lakshan.medical_records_service.service;

import com.lakshan.medical_records_service.client.ConsultationClient;
import com.lakshan.medical_records_service.domain.TestResult;
import com.lakshan.medical_records_service.dto.TestResultDtos;
import com.lakshan.medical_records_service.repository.TestResultRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestResultService {

    private final TestResultRepository repository;
    private final FileStorageService fileStorageService;
    private final ConsultationClient consultationClient;

    public TestResultService(TestResultRepository repository, 
                           FileStorageService fileStorageService,
                           ConsultationClient consultationClient) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        this.consultationClient = consultationClient;
    }

    @Transactional
    public TestResultDtos.Response createTestResult(TestResultDtos.CreateRequest req, MultipartFile file) {
        if (repository.existsByLabTestId(req.labTestId())) {
            throw new IllegalArgumentException("Test result already exists for this lab test");
        }

        TestResult testResult = new TestResult();
        testResult.setLabTestId(req.labTestId());
        testResult.setPatientId(req.patientId());
        testResult.setTechnicianId(req.technicianId());
        testResult.setTestResultDescription(req.testResultDescription());
        testResult.setTechnicianNotes(req.technicianNotes());

        if (file != null && !file.isEmpty()) {
            if (!fileStorageService.isValidFileType(file.getContentType())) {
                throw new IllegalArgumentException("Invalid file type. Only PDF, DOC, DOCX, JPG, PNG allowed");
            }
            String fileName = fileStorageService.storeFile(file);
            testResult.setFileName(file.getOriginalFilename());
            testResult.setFilePath(fileName);
            testResult.setFileType(file.getContentType());
            testResult.setFileSize(file.getSize());
        }

        TestResult saved = repository.save(testResult);

        // Update lab test status to COMPLETED
        try {
            consultationClient.updateLabTestStatus(
                req.labTestId(),
                new ConsultationClient.StatusUpdateRequest("COMPLETED", req.testResultDescription(), req.technicianNotes())
            );
        } catch (Exception e) {
            System.err.println("Failed to update lab test status: " + e.getMessage());
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TestResultDtos.Response getTestResult(Long id) {
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found"));
        return toResponse(testResult);
    }

    @Transactional(readOnly = true)
    public TestResultDtos.Response getTestResultByLabTestId(Long labTestId) {
        TestResult testResult = repository.findByLabTestId(labTestId)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found for lab test"));
        return toResponse(testResult);
    }

    @Transactional(readOnly = true)
    public List<TestResultDtos.Response> getTestResultsByPatient(Long patientId) {
        return repository.findByPatientId(patientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TestResultDtos.Response> getTestResultsByPatientPaged(Long patientId, Pageable pageable) {
        return repository.findByPatientId(patientId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public byte[] downloadFile(Long id) {
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found"));
        
        if (testResult.getFilePath() == null) {
            throw new IllegalArgumentException("No file attached to this test result");
        }

        try {
            return java.nio.file.Files.readAllBytes(fileStorageService.loadFile(testResult.getFilePath()));
        } catch (Exception e) {
            throw new RuntimeException("Could not read file", e);
        }
    }

    @Transactional
    public TestResultDtos.Response updateTestResult(Long id, String testResultDescription, String technicianNotes, MultipartFile file) {
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found"));

        if (testResultDescription != null) {
            testResult.setTestResultDescription(testResultDescription);
        }
        if (technicianNotes != null) {
            testResult.setTechnicianNotes(technicianNotes);
        }

        if (file != null && !file.isEmpty()) {
            if (!fileStorageService.isValidFileType(file.getContentType())) {
                throw new IllegalArgumentException("Invalid file type. Only PDF, DOC, DOCX, JPG, PNG allowed");
            }
            // Delete old file if exists
            if (testResult.getFilePath() != null) {
                fileStorageService.deleteFile(testResult.getFilePath());
            }
            // Store new file
            String fileName = fileStorageService.storeFile(file);
            testResult.setFileName(file.getOriginalFilename());
            testResult.setFilePath(fileName);
            testResult.setFileType(file.getContentType());
            testResult.setFileSize(file.getSize());
        }

        TestResult updated = repository.save(testResult);
        return toResponse(updated);
    }

    @Transactional
    public void deleteTestResult(Long id) {
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test result not found"));
        
        // Delete file if exists
        if (testResult.getFilePath() != null) {
            fileStorageService.deleteFile(testResult.getFilePath());
        }
        
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<TestResultDtos.Response> getAllTestResults(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    private TestResultDtos.Response toResponse(TestResult testResult) {
        return new TestResultDtos.Response(
                testResult.getId(),
                testResult.getLabTestId(),
                testResult.getPatientId(),
                testResult.getTechnicianId(),
                testResult.getTestResultDescription(),
                testResult.getTechnicianNotes(),
                testResult.getFilePath(),
                testResult.getFileName(),
                testResult.getFileType(),
                testResult.getFileSize(),
                testResult.getCreatedAt(),
                testResult.getUpdatedAt()
        );
    }
}
