package com.lakshan.medical_records_service.web;

import com.lakshan.medical_records_service.dto.TestResultDtos;
import com.lakshan.medical_records_service.service.TestResultService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/test-results")
@Validated
public class TestResultController {

    private static final int MAX_PAGE_SIZE = 100;

    private final TestResultService service;

    public TestResultController(TestResultService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestResultDtos.Response> createTestResult(
            @RequestParam("labTestId") @NotNull @Positive Long labTestId,
            @RequestParam("patientId") @NotNull @Positive Long patientId,
            @RequestParam("technicianId") @NotNull @Positive Long technicianId,
            @RequestParam("testResultDescription") @NotBlank @Size(max = 4000) String testResultDescription,
            @RequestParam(value = "technicianNotes", required = false) @Size(max = 4000) String technicianNotes,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        TestResultDtos.CreateRequest req = new TestResultDtos.CreateRequest(
                labTestId, patientId, technicianId, testResultDescription, technicianNotes
        );
        return ResponseEntity.ok(service.createTestResult(req, files));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResultDtos.Response> getTestResult(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getTestResult(id));
    }

    @GetMapping("/lab-test/{labTestId}")
    public ResponseEntity<TestResultDtos.Response> getTestResultByLabTest(@PathVariable("labTestId") Long labTestId) {
        return ResponseEntity.ok(service.getTestResultByLabTestId(labTestId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TestResultDtos.Response>> getTestResultsByPatient(@PathVariable("patientId") Long patientId) {
        return ResponseEntity.ok(service.getTestResultsByPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/paged")
    public ResponseEntity<Page<TestResultDtos.Response>> getTestResultsByPatientPaged(
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(service.getTestResultsByPatientPaged(patientId, pageable));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<TestResultDtos.Response>> getTestResultsByTechnician(@PathVariable("technicianId") Long technicianId) {
        return ResponseEntity.ok(service.getTestResultsByTechnician(technicianId));
    }

    @GetMapping("/technician/{technicianId}/paged")
    public ResponseEntity<Page<TestResultDtos.Response>> getTestResultsByTechnicianPaged(
            @PathVariable("technicianId") Long technicianId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(service.getTestResultsByTechnicianPaged(technicianId, pageable));
    }

    @GetMapping("/{id}/files/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Long id, @PathVariable("fileId") Long fileId) {
        TestResultService.TestResultFileDownload download = service.downloadFile(id, fileId);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(sanitizeFilename(download.fileName()), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(download.fileType()))
                .body(download.data());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestResultDtos.Response> updateTestResult(
            @PathVariable("id") Long id,
            @RequestParam(value = "testResultDescription", required = false) @Size(max = 4000) String testResultDescription,
            @RequestParam(value = "technicianNotes", required = false) @Size(max = 4000) String technicianNotes,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "removeFileIds", required = false) List<Long> removeFileIds) {

        return ResponseEntity.ok(service.updateTestResult(id, testResultDescription, technicianNotes, files, removeFileIds));
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestResultDtos.Response> updateTestResultPost(
            @PathVariable("id") Long id,
            @RequestParam(value = "testResultDescription", required = false) @Size(max = 4000) String testResultDescription,
            @RequestParam(value = "technicianNotes", required = false) @Size(max = 4000) String technicianNotes,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "removeFileIds", required = false) List<Long> removeFileIds) {

        return ResponseEntity.ok(service.updateTestResult(id, testResultDescription, technicianNotes, files, removeFileIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable("id") Long id) {
        service.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteTestResultPost(@PathVariable("id") Long id) {
        service.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TestResultDtos.Response>> getAllTestResults(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(service.getAllTestResults(pageable));
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(safePage, safeSize, sort);
    }

    private String sanitizeFilename(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "file";
        }
        return fileName.replaceAll("[\\r\\n\\x00-\\x1F\\x7F]", "").replace("\"", "'");
    }
}
