package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.PatientProfile;
import com.lakshan.user_service.service.PatientProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient_profile")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientProfileController {

    private final PatientProfileService patientProfileService;

    @Autowired
    public PatientProfileController(PatientProfileService patientProfileService) {
        this.patientProfileService = patientProfileService;
    }

    @PostMapping("/createPatientProfile")
    public ResponseEntity<PatientProfile> createPatientProfile(@RequestBody PatientProfile patientProfile) {
        patientProfileService.createNewPatientProfile(patientProfile);
        return ResponseEntity.status(201).body(patientProfile);
    }

    @GetMapping("/getPatientProfile/{id}")
    public PatientProfile getPatientProfile(@PathVariable int id) {
        return patientProfileService.getPatientProfileById(id);
    }

    @GetMapping("/getAllPatientProfiles")
    public List<PatientProfile> getAllPatientProfiles() {
        return patientProfileService.getAllPatientProfiles();
    }

    @GetMapping("/getPatientProfileByUserId/{userId}")
    public PatientProfile getPatientProfileByUserId(@PathVariable int userId) {
        return patientProfileService.getPatientProfileByUserId(userId);
    }

    @PutMapping("/updatePatientProfile")
    public ResponseEntity<PatientProfile> updatePatientProfile(@RequestBody PatientProfile patientProfile) {
        patientProfileService.updatePatientProfile(patientProfile);
        return ResponseEntity.ok(patientProfile);
    }

    @DeleteMapping("/deletePatientProfile/{id}")
    public void deletePatientProfile(@PathVariable int id) {
        patientProfileService.deletePatientProfile(id);
    }

}
