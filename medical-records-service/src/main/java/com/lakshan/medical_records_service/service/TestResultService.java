package com.lakshan.medical_records_service.service;

import com.lakshan.medical_records_service.client.ConsultationClient;
import com.lakshan.medical_records_service.client.UserClient;
import com.lakshan.medical_records_service.domain.TestResult;
import com.lakshan.medical_records_service.domain.TestResultFile;
import com.lakshan.medical_records_service.dto.TestResultDtos;
import com.lakshan.medical_records_service.exception.ForbiddenException;
import com.lakshan.medical_records_service.exception.TestResultNotFoundException;
import com.lakshan.medical_records_service.repository.TestResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestResultService {

    private static final Logger log = LoggerFactory.getLogger(TestResultService.class);
    public static final int MAX_FILES_PER_RESULT = 5;
    private static final String ROLE_PATIENT = "PATIENT";
    private static final String ROLE_TECHNICIAN = "TECHNICIAN";
    private static final String ROLE_ADMIN = "ADMIN";

    private final TestResultRepository repository;
    private final FileStorageService fileStorageService;
    private final ConsultationClient consultationClient;
    private final UserClient userClient;

    public TestResultService(TestResultRepository repository,
                           FileStorageService fileStorageService,
                           ConsultationClient consultationClient,
                           UserClient userClient) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
        this.consultationClient = consultationClient;
        this.userClient = userClient;
    }

    public record RequesterContext(String email, String role) {}

    private boolean isStaff(String role) {
        return role != null && (role.equalsIgnoreCase(ROLE_TECHNICIAN) || role.equalsIgnoreCase(ROLE_ADMIN));
    }

    private void requireStaffRole(RequesterContext requester) {
        if (!isStaff(requester.role())) {
            throw new ForbiddenException("Only technicians or admins may perform this action");
        }
    }

    private void requireOwnerOrStaff(RequesterContext requester, Long patientId) {
        if (isStaff(requester.role())) {
            return;
        }
        if (requester.role() != null && requester.role().equalsIgnoreCase(ROLE_PATIENT)) {
            Long callerId = resolveUserId(requester.email());
            if (callerId != null && callerId.equals(patientId)) {
                return;
            }
        }
        throw new ForbiddenException("You do not have access to this test result");
    }

    private Long resolveUserId(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        try {
            UserClient.UserLookupResponse user = userClient.getUserByEmail(email);
            return user != null ? user.id() : null;
        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to resolve identity for email={}", email, e);
            return null;
        }
    }

    @Transactional
    public TestResultDtos.Response createTestResult(RequesterContext requester, TestResultDtos.CreateRequest req, List<MultipartFile> files) {
        requireStaffRole(requester);
        if (repository.existsByLabTestId(req.labTestId())) {
            throw new IllegalArgumentException("A test result for this lab test already exists");
        }

        List<MultipartFile> uploads = usableFiles(files);
        if (uploads.size() > MAX_FILES_PER_RESULT) {
            throw new IllegalArgumentException("A maximum of " + MAX_FILES_PER_RESULT + " files is allowed");
        }
        uploads.forEach(this::validateFile);

        TestResult testResult = new TestResult();
        testResult.setLabTestId(req.labTestId());
        testResult.setPatientId(req.patientId());
        testResult.setTechnicianId(req.technicianId());
        testResult.setTestResultDescription(req.testResultDescription());
        testResult.setTechnicianNotes(req.technicianNotes());

        List<String> storedFileNames = new ArrayList<>();
        for (MultipartFile upload : uploads) {
            String storedFileName = fileStorageService.storeFile(upload);
            storedFileNames.add(storedFileName);
            testResult.getFiles().add(toFileEntity(testResult, upload, storedFileName));
        }

        TestResult saved;
        try {
            saved = repository.save(testResult);
        } catch (DataIntegrityViolationException e) {
            storedFileNames.forEach(fileStorageService::deleteFile);
            log.warn("Duplicate test result creation attempt for labTestId={}", req.labTestId());
            throw new IllegalArgumentException("A test result for this lab test already exists");
        } catch (RuntimeException e) {
            storedFileNames.forEach(fileStorageService::deleteFile);
            throw e;
        }

        try {
            consultationClient.updateLabTestStatus(
                req.labTestId(),
                new ConsultationClient.StatusUpdateRequest("COMPLETED", req.testResultDescription(), req.technicianNotes())
            );
        } catch (Exception e) {
            log.error("Test result {} saved but failed to update lab test {} status to COMPLETED",
                    saved.getId(), req.labTestId(), e);
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TestResultDtos.Response getTestResult(RequesterContext requester, Long id) {
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found"));
        requireOwnerOrStaff(requester, testResult.getPatientId());
        return toResponse(testResult);
    }

    @Transactional(readOnly = true)
    public TestResultDtos.Response getTestResultByLabTestId(RequesterContext requester, Long labTestId) {
        TestResult testResult = repository.findByLabTestId(labTestId)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found for lab test"));
        requireOwnerOrStaff(requester, testResult.getPatientId());
        return toResponse(testResult);
    }

    @Transactional(readOnly = true)
    public List<TestResultDtos.Response> getTestResultsByPatient(RequesterContext requester, Long patientId) {
        requireOwnerOrStaff(requester, patientId);
        return repository.findByPatientId(patientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TestResultDtos.Response> getTestResultsByPatientPaged(RequesterContext requester, Long patientId, Pageable pageable) {
        requireOwnerOrStaff(requester, patientId);
        return repository.findByPatientId(patientId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TestResultDtos.Response> getTestResultsByTechnician(RequesterContext requester, Long technicianId) {
        requireStaffRole(requester);
        return repository.findByTechnicianId(technicianId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TestResultDtos.Response> getTestResultsByTechnicianPaged(RequesterContext requester, Long technicianId, Pageable pageable) {
        requireStaffRole(requester);
        return repository.findByTechnicianId(technicianId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TestResultFileDownload downloadFile(RequesterContext requester, Long testResultId, Long fileId) {
        TestResult testResult = repository.findById(testResultId)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found"));
        requireOwnerOrStaff(requester, testResult.getPatientId());

        TestResultFile file = testResult.getFiles().stream()
                .filter(f -> f.getId().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new TestResultNotFoundException("File not found for this test result"));

        try {
            Path path = fileStorageService.loadFile(file.getFilePath());
            byte[] bytes = java.nio.file.Files.readAllBytes(path);
            return new TestResultFileDownload(bytes, file.getFileName(), file.getFileType());
        } catch (Exception e) {
            log.error("Failed to read file {} for test result {}", fileId, testResultId, e);
            throw new RuntimeException("Could not read the stored file for this test result");
        }
    }

    @Transactional
    public TestResultDtos.Response updateTestResult(RequesterContext requester, Long id, String testResultDescription, String technicianNotes,
                                                      List<MultipartFile> newFiles, List<Long> removeFileIds) {
        requireStaffRole(requester);
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found"));

        if (testResultDescription != null) {
            testResult.setTestResultDescription(testResultDescription);
        }
        if (technicianNotes != null) {
            testResult.setTechnicianNotes(technicianNotes);
        }

        List<Long> idsToRemove = removeFileIds != null ? removeFileIds : Collections.emptyList();
        List<TestResultFile> toRemove = testResult.getFiles().stream()
                .filter(f -> idsToRemove.contains(f.getId()))
                .collect(Collectors.toList());

        List<MultipartFile> uploads = usableFiles(newFiles);
        int remainingAfterRemoval = testResult.getFiles().size() - toRemove.size();
        if (remainingAfterRemoval + uploads.size() > MAX_FILES_PER_RESULT) {
            throw new IllegalArgumentException("A maximum of " + MAX_FILES_PER_RESULT + " files is allowed");
        }
        uploads.forEach(this::validateFile);

        testResult.getFiles().removeAll(toRemove);

        List<String> storedFileNames = new ArrayList<>();
        for (MultipartFile upload : uploads) {
            String storedFileName = fileStorageService.storeFile(upload);
            storedFileNames.add(storedFileName);
            testResult.getFiles().add(toFileEntity(testResult, upload, storedFileName));
        }

        TestResult updated;
        try {
            updated = repository.save(testResult);
        } catch (RuntimeException e) {
            storedFileNames.forEach(fileStorageService::deleteFile);
            throw e;
        }

        toRemove.forEach(f -> fileStorageService.deleteFile(f.getFilePath()));

        return toResponse(updated);
    }

    @Transactional
    public void deleteTestResult(RequesterContext requester, Long id) {
        requireStaffRole(requester);
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found"));

        Long labTestId = testResult.getLabTestId();
        List<String> filePathsToDelete = testResult.getFiles().stream()
                .map(TestResultFile::getFilePath)
                .collect(Collectors.toList());

        repository.deleteById(id);

        filePathsToDelete.forEach(fileStorageService::deleteFile);

        try {
            consultationClient.startLabTest(labTestId);
        } catch (Exception e) {
            log.error("Test result {} deleted but failed to revert lab test {} status to IN_PROGRESS", id, labTestId, e);
        }
    }

    @Transactional(readOnly = true)
    public Page<TestResultDtos.Response> getAllTestResults(RequesterContext requester, Pageable pageable) {
        requireStaffRole(requester);
        return repository.findAll(pageable).map(this::toResponse);
    }

    private List<MultipartFile> usableFiles(List<MultipartFile> files) {
        if (files == null) return Collections.emptyList();
        return files.stream().filter(f -> f != null && !f.isEmpty()).collect(Collectors.toList());
    }

    private TestResultFile toFileEntity(TestResult testResult, MultipartFile upload, String storedFileName) {
        TestResultFile file = new TestResultFile();
        file.setTestResult(testResult);
        file.setFileName(upload.getOriginalFilename());
        file.setFilePath(storedFileName);
        file.setFileType(upload.getContentType());
        file.setFileSize(upload.getSize());
        return file;
    }

    private void validateFile(MultipartFile file) {
        if (!fileStorageService.isValidFileType(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only PDF, DOC, DOCX, JPG, PNG allowed");
        }
        try {
            if (!fileStorageService.contentMatchesDeclaredType(file)) {
                throw new IllegalArgumentException("File content does not match the declared file type");
            }
        } catch (IOException e) {
            log.error("Failed to inspect uploaded file content", e);
            throw new IllegalArgumentException("Could not read the uploaded file");
        }
    }

    private TestResultDtos.Response toResponse(TestResult testResult) {
        List<TestResultDtos.FileInfo> fileInfos = testResult.getFiles().stream()
                .map(f -> new TestResultDtos.FileInfo(f.getId(), f.getFileName(), f.getFileType(), f.getFileSize(), f.getUploadedAt()))
                .collect(Collectors.toList());
        return new TestResultDtos.Response(
                testResult.getId(),
                testResult.getLabTestId(),
                testResult.getPatientId(),
                testResult.getTechnicianId(),
                testResult.getTestResultDescription(),
                testResult.getTechnicianNotes(),
                fileInfos,
                testResult.getCreatedAt(),
                testResult.getUpdatedAt()
        );
    }

    public record TestResultFileDownload(byte[] data, String fileName, String fileType) {}
}
