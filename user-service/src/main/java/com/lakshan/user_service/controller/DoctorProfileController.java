package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.DoctorProfile;
import com.lakshan.user_service.service.DoctorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor_profile")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    @Autowired
    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @PostMapping("/createDoctorProfile")
    public ResponseEntity<DoctorProfile> createDoctorProfile(@RequestBody DoctorProfile doctorProfile) {
        doctorProfileService.createNewDoctorProfile(doctorProfile);
        return ResponseEntity.status(201).body(doctorProfile);
    }

    @GetMapping("/getDoctorProfile/{id}")
    public DoctorProfile getDoctorProfile(@PathVariable int id) {
        return doctorProfileService.getDoctorProfileById(id);
    }

    @GetMapping("/getDoctorProfileByUserId/{userId}")
    public DoctorProfile getDoctorProfileByUserId(@PathVariable int userId) {
        return doctorProfileService.getDoctorProfileByUserId(userId);
    }

    @GetMapping("/getAllDoctorProfiles")
    public List<DoctorProfile> getAllDoctorProfiles() {
        return doctorProfileService.getAllDoctorProfiles();
    }

    @PutMapping("/updateDoctorProfile")
    public ResponseEntity<DoctorProfile> updateDoctorProfile(@RequestBody DoctorProfile doctorProfile){
        doctorProfileService.updateDoctorProfile(doctorProfile);
        return ResponseEntity.ok(doctorProfile);
    }

    @DeleteMapping("/deleteDoctorProfile/{id}")
    public void deleteDoctorProfile(@PathVariable int id) {
        doctorProfileService.deleteDoctorProfile(id);
    }
}
