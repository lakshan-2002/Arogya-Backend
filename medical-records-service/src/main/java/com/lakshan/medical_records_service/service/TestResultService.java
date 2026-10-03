package com.lakshan.medical_records_service.service;

import com.lakshan.medical_records_service.client.ConsultationClient;
import com.lakshan.medical_records_service.domain.TestResult;
import com.lakshan.medical_records_service.domain.TestResultFile;
import com.lakshan.medical_records_service.dto.TestResultDtos;
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
    public TestResultDtos.Response createTestResult(TestResultDtos.CreateRequest req, List<MultipartFile> files) {
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
    public TestResultDtos.Response getTestResult(Long id) {
        TestResult testResult = repository.findById(id)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found"));
        return toResponse(testResult);
    }

    @Transactional(readOnly = true)
    public TestResultDtos.Response getTestResultByLabTestId(Long labTestId) {
        TestResult testResult = repository.findByLabTestId(labTestId)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found for lab test"));
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
    public List<TestResultDtos.Response> getTestResultsByTechnician(Long technicianId) {
        return repository.findByTechnicianId(technicianId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TestResultDtos.Response> getTestResultsByTechnicianPaged(Long technicianId, Pageable pageable) {
        return repository.findByTechnicianId(technicianId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TestResultFileDownload downloadFile(Long testResultId, Long fileId) {
        TestResult testResult = repository.findById(testResultId)
                .orElseThrow(() -> new TestResultNotFoundException("Test result not found"));

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
    public TestResultDtos.Response updateTestResult(Long id, String testResultDescription, String technicianNotes,
                                                      List<MultipartFile> newFiles, List<Long> removeFileIds) {
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
    public void deleteTestResult(Long id) {
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
    public Page<TestResultDtos.Response> getAllTestResults(Pageable pageable) {
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
