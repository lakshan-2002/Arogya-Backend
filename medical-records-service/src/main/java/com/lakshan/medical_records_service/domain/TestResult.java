package com.lakshan.medical_records_service.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_results", indexes = {
        @Index(name = "idx_lab_test_id", columnList = "lab_test_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_technician_id", columnList = "technician_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lab_test_id", nullable = false, unique = true)
    private Long labTestId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "technician_id", nullable = false)
    private Long technicianId;

    @Column(name = "test_result_description", columnDefinition = "TEXT", nullable = false)
    private String testResultDescription;

    @Column(name = "technician_notes", columnDefinition = "TEXT")
    private String technicianNotes;

    @OneToMany(mappedBy = "testResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TestResultFile> files = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLabTestId() { return labTestId; }
    public void setLabTestId(Long labTestId) { this.labTestId = labTestId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getTechnicianId() { return technicianId; }
    public void setTechnicianId(Long technicianId) { this.technicianId = technicianId; }

    public String getTestResultDescription() { return testResultDescription; }
    public void setTestResultDescription(String testResultDescription) { this.testResultDescription = testResultDescription; }

    public String getTechnicianNotes() { return technicianNotes; }
    public void setTechnicianNotes(String technicianNotes) { this.technicianNotes = technicianNotes; }

    public List<TestResultFile> getFiles() { return files; }
    public void setFiles(List<TestResultFile> files) { this.files = files; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
