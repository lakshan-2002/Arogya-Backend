package com.lakshan.user_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "technician_profile")
public class TechnicianProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "technician_id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "nic_number")
    private String nicNumber;

    @Column(name = "technician_field")
    private String technicianField;

    @Column(name = "licence_number")
    private String licenceNumber;

    @Column(name = "certification")
    private String certification;

    @Column(name = "assigned_equipment")
    private String assignedEquipment;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNicNumber() {
        return nicNumber;
    }

    public void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public String getTechnicianField() {
        return technicianField;
    }

    public void setTechnicianField(String technicianField) {
        this.technicianField = technicianField;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getAssignedEquipment() {
        return assignedEquipment;
    }

    public void setAssignedEquipment(String assignedEquipment) {
        this.assignedEquipment = assignedEquipment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
