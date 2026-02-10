package com.lakshan.medical_records_service.web;

import com.lakshan.medical_records_service.dto.TestResultDtos;
import com.lakshan.medical_records_service.service.TestResultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/test-results")
@Validated
public class TestResultController {

    private final TestResultService service;

    public TestResultController(TestResultService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestResultDtos.Response> createTestResult(
            @RequestParam Long labTestId,
            @RequestParam Long patientId,
            @RequestParam Long technicianId,
            @RequestParam String testResultDescription,
            @RequestParam(required = false) String technicianNotes,
            @RequestParam(required = false) MultipartFile file) {
        
        TestResultDtos.CreateRequest req = new TestResultDtos.CreateRequest(
                labTestId, patientId, technicianId, testResultDescription, technicianNotes
        );
        return ResponseEntity.ok(service.createTestResult(req, file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestResultDtos.Response> getTestResult(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTestResult(id));
    }

    @GetMapping("/lab-test/{labTestId}")
    public ResponseEntity<TestResultDtos.Response> getTestResultByLabTest(@PathVariable Long labTestId) {
        return ResponseEntity.ok(service.getTestResultByLabTestId(labTestId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TestResultDtos.Response>> getTestResultsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.getTestResultsByPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/paged")
    public ResponseEntity<Page<TestResultDtos.Response>> getTestResultsByPatientPaged(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(service.getTestResultsByPatientPaged(patientId, pageable));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        byte[] fileData = service.downloadFile(id);
        TestResultDtos.Response testResult = service.getTestResult(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + testResult.fileName() + "\"")
                .contentType(MediaType.parseMediaType(testResult.fileType()))
                .body(fileData);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestResultDtos.Response> updateTestResult(
            @PathVariable Long id,
            @RequestParam(required = false) String testResultDescription,
            @RequestParam(required = false) String technicianNotes,
            @RequestParam(required = false) MultipartFile file) {
        
        return ResponseEntity.ok(service.updateTestResult(id, testResultDescription, technicianNotes, file));
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestResultDtos.Response> updateTestResultPost(
            @PathVariable Long id,
            @RequestParam(required = false) String testResultDescription,
            @RequestParam(required = false) String technicianNotes,
            @RequestParam(required = false) MultipartFile file) {
        
        return ResponseEntity.ok(service.updateTestResult(id, testResultDescription, technicianNotes, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable Long id) {
        service.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TestResultDtos.Response>> getAllTestResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(service.getAllTestResults(pageable));
    }
}
