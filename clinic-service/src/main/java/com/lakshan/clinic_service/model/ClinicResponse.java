package com.lakshan.clinic_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClinicResponse implements Serializable {

    private String clinicName;
    private String province;
    private String district;
    private String location;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private String status;

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ClinicResponse{" +
                "clinicName='" + clinicName + '\'' +
                ", province='" + province + '\'' +
                ", district='" + district + '\'' +
                ", location='" + location + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", scheduledTime=" + scheduledTime +
                ", status='" + status + '\'' +
                '}';
    }
}
