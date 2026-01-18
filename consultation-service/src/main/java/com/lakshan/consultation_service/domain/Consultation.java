package com.lakshan.consultation_service.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations", indexes = {
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_clinic_id", columnList = "clinic_id"),
        @Index(name = "idx_queue_token_id", columnList = "queue_token_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_booked_at", columnList = "booked_at"),
        @Index(name = "idx_patient_doctor", columnList = "patient_id,doctor_id")
})
public class Consultation {

    public enum Status { SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @Column(name = "queue_token_id")
    private Long queueTokenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.SCHEDULED;

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(name = "past_medical_history", columnDefinition = "TEXT")
    private String pastMedicalHistory;

    @Column(name = "present_illness", columnDefinition = "TEXT")
    private String presentIllness;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "session_number", nullable = false)
    private Integer sessionNumber = 1;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getClinicId() { return clinicId; }
    public void setClinicId(Long clinicId) { this.clinicId = clinicId; }
    public Long getQueueTokenId() { return queueTokenId; }
    public void setQueueTokenId(Long queueTokenId) { this.queueTokenId = queueTokenId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getPastMedicalHistory() { return pastMedicalHistory; }
    public void setPastMedicalHistory(String pastMedicalHistory) { this.pastMedicalHistory = pastMedicalHistory; }
    public String getPresentIllness() { return presentIllness; }
    public void setPresentIllness(String presentIllness) { this.presentIllness = presentIllness; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public Integer getSessionNumber() { return sessionNumber; }
    public void setSessionNumber(Integer sessionNumber) { this.sessionNumber = sessionNumber; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
