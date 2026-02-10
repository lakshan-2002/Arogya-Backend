package com.lakshan.consultation_service.web;

import com.lakshan.consultation_service.domain.LabTest.TestStatus;
import com.lakshan.consultation_service.dto.LabTestDtos;
import com.lakshan.consultation_service.service.LabTestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/lab-tests")
@Validated
public class LabTestController {

    private final LabTestService service;

    public LabTestController(LabTestService service) {
        this.service = service;
    }

    /**
     * Create a new lab test (typically called by doctor during consultation)
     */
    @PostMapping
    public ResponseEntity<LabTestDtos.Response> createTest(@RequestBody @Validated LabTestDtos.CreateRequest req) {
        return ResponseEntity.ok(service.createTest(req));
    }

    /**
     * Get a specific lab test by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LabTestDtos.Response> getTest(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTest(id));
    }

    /**
     * Get all lab tests for a specific consultation
     */
    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<List<LabTestDtos.Response>> getTestsByConsultation(@PathVariable Long consultationId) {
        return ResponseEntity.ok(service.getTestsByConsultation(consultationId));
    }

    /**
     * List lab tests with filters (for doctors/admins)
     */
    @GetMapping
    public ResponseEntity<Page<LabTestDtos.Response>> listTests(
            @RequestParam(required = false) TestStatus status,
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(service.listTests(status, technicianId, start, end, pageable));
    }

    /**
     * Update a lab test (for doctors/admins)
     */
    @PutMapping("/{id}")
    public ResponseEntity<LabTestDtos.Response> updateTest(
            @PathVariable Long id, 
            @RequestBody LabTestDtos.UpdateRequest req) {
        return ResponseEntity.ok(service.updateTest(id, req));
    }

    /**
     * Delete a lab test
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        service.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * TECHNICIAN DASHBOARD - Get all lab tests visible to technicians
     * This is the main endpoint for the technician dashboard
     */
    @GetMapping("/technician/dashboard")
    public ResponseEntity<Page<LabTestDtos.TechnicianDashboardResponse>> getTechnicianDashboard(
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) TestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(service.getTechnicianDashboard(technicianId, status, pageable));
    }

    /**
     * Update test by technician (limited fields)
     * Technicians can update test results, notes, and status
     */
    @PutMapping("/{id}/technician-update")
    public ResponseEntity<LabTestDtos.Response> updateByTechnician(
            @PathVariable Long id,
            @RequestBody LabTestDtos.TechnicianUpdateRequest req) {
        return ResponseEntity.ok(service.updateTestByTechnician(id, req));
    }

    /**
     * Assign a test to a technician
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<LabTestDtos.Response> assignToTechnician(
            @PathVariable Long id,
            @RequestParam Long technicianId) {
        LabTestDtos.UpdateRequest req = new LabTestDtos.UpdateRequest(
                null, null, null, technicianId, null, null
        );
        return ResponseEntity.ok(service.updateTest(id, req));
    }

    /**
     * Start a test (change status to IN_PROGRESS)
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<LabTestDtos.Response> startTest(@PathVariable Long id) {
        LabTestDtos.TechnicianUpdateRequest req = new LabTestDtos.TechnicianUpdateRequest(
                TestStatus.IN_PROGRESS, null, null
        );
        return ResponseEntity.ok(service.updateTestByTechnician(id, req));
    }

    /**
     * Complete a test (change status to COMPLETED)
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<LabTestDtos.Response> completeTest(
            @PathVariable Long id,
            @RequestBody(required = false) LabTestDtos.TechnicianUpdateRequest req) {
        LabTestDtos.TechnicianUpdateRequest updateReq = new LabTestDtos.TechnicianUpdateRequest(
                TestStatus.COMPLETED,
                req != null ? req.testResults() : null,
                req != null ? req.technicianNotes() : null
        );
        return ResponseEntity.ok(service.updateTestByTechnician(id, updateReq));
    }

    /**
     * Cancel a test
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<LabTestDtos.Response> cancelTest(@PathVariable Long id) {
        LabTestDtos.TechnicianUpdateRequest req = new LabTestDtos.TechnicianUpdateRequest(
                TestStatus.CANCELLED, null, null
        );
        return ResponseEntity.ok(service.updateTestByTechnician(id, req));
    }
}
