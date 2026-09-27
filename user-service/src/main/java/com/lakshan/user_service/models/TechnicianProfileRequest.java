package com.lakshan.user_service.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class TechnicianProfileRequest implements Serializable {

    private int id;
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    private String dateOfBirth;
    private String phoneNumber;
    private String nicNumber;
    private String technicianField;
    private String licenseNumber;
    private String certification;
    private String assignedEquipment;

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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
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
}
