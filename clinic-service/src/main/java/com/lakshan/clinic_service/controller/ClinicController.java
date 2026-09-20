package com.lakshan.clinic_service.controller;

import com.lakshan.clinic_service.entity.Clinic;
import com.lakshan.clinic_service.model.ClinicRequest;
import com.lakshan.clinic_service.model.ClinicResponse;
import com.lakshan.clinic_service.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    @Autowired
    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping("/createClinic")
    public ResponseEntity<ClinicResponse> createClinic(@RequestBody ClinicRequest clinicRequest) {
        ClinicResponse clinicResponse = new ClinicResponse();
        clinicResponse.setClinicName(clinicRequest.getClinicName());
        clinicResponse.setProvince(clinicRequest.getProvince());
        clinicResponse.setDistrict(clinicRequest.getDistrict());
        clinicResponse.setLocation(clinicRequest.getLocation());
        clinicResponse.setScheduledDate(clinicRequest.getScheduledDate());
        clinicResponse.setScheduledTime(clinicRequest.getScheduledTime());
        clinicResponse.setStatus(clinicRequest.getStatus());

        clinicService.createClinic(clinicRequest);
        return ResponseEntity.status(201).body(clinicResponse);
    }

    @GetMapping("getClinic/{id}")
    public Clinic getClinic(@PathVariable int id) {
        return clinicService.getClinicById(id);
    }

    @GetMapping("/getAllClinics")
    public List<Clinic> getAllClinics() {
        return clinicService.getAllClinics();
    }

    @PutMapping("/updateClinic")
    public ResponseEntity<ClinicResponse> updateClinic(@RequestBody ClinicRequest clinicRequest) {
        ClinicResponse clinicResponse = new ClinicResponse();
        clinicResponse.setClinicName(clinicRequest.getClinicName());
        clinicResponse.setProvince(clinicRequest.getProvince());
        clinicResponse.setDistrict(clinicRequest.getDistrict());
        clinicResponse.setLocation(clinicRequest.getLocation());
        clinicResponse.setScheduledDate(clinicRequest.getScheduledDate());
        clinicResponse.setScheduledTime(clinicRequest.getScheduledTime());
        clinicResponse.setStatus(clinicRequest.getStatus());

        clinicService.updateClinic(clinicRequest);
        return ResponseEntity.ok(clinicResponse);
    }

    @DeleteMapping("/deleteClinic/{id}")
    public void deleteClinic(@PathVariable int id) {
        clinicService.deleteClinic(id);
    }
}
