package com.lakshan.clinic_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clinic_doctors")
public class ClinicDoctors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "doctor_ref_id")
    private int doctorRefId; // Not a foreign key, just a reference ID

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "specialization")
    private String specialization;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorRefId() {
        return doctorRefId;
    }

    public void setDoctorRefId(int doctorRefId) {
        this.doctorRefId = doctorRefId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
}
