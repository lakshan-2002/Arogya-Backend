package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.PatientProfile;
import com.lakshan.user_service.models.PatientProfileRequest;
import com.lakshan.user_service.models.PatientProfileResponse;
import com.lakshan.user_service.service.PatientProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient_profile")
public class PatientProfileController {

    private final PatientProfileService patientProfileService;

    @Autowired
    public PatientProfileController(PatientProfileService patientProfileService) {
        this.patientProfileService = patientProfileService;
    }

    @PostMapping("/createPatientProfile")
    public ResponseEntity<PatientProfileResponse> createPatientProfile(
            @Valid @RequestBody PatientProfileRequest patientProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        patientProfileService.createNewPatientProfile(patientProfileRequest, email);

        PatientProfileResponse patientProfileResponse = new PatientProfileResponse();
        patientProfileResponse.setFirstName(patientProfileRequest.getFirstName());
        patientProfileResponse.setLastName(patientProfileRequest.getLastName());
        patientProfileResponse.setDateOfBirth(patientProfileRequest.getDateOfBirth());
        patientProfileResponse.setPhoneNumber(patientProfileRequest.getPhoneNumber());
        patientProfileResponse.setNicNumber(patientProfileRequest.getNicNumber());
        patientProfileResponse.setAddress(patientProfileRequest.getAddress());
        patientProfileResponse.setGender(patientProfileRequest.getGender());
        patientProfileResponse.setBloodGroup(patientProfileRequest.getBloodGroup());
        patientProfileResponse.setAllergies(patientProfileRequest.getAllergies());
        patientProfileResponse.setChronicDiseases(patientProfileRequest.getChronicDiseases());
        patientProfileResponse.setEmergencyContact(patientProfileRequest.getEmergencyContact());

        return ResponseEntity.status(201).body(patientProfileResponse);
    }

    @GetMapping("/getAllPatientProfiles")
    public List<PatientProfile> getAllPatientProfiles() {
        return patientProfileService.getAllPatientProfiles();
    }

    @GetMapping("/getPatientProfileByUserId/{userId}")
    public PatientProfile getPatientProfileByUserId(@PathVariable("userId") int userId) {
        return patientProfileService.getPatientProfileByUserId(userId);
    }

    @GetMapping("/getPatientProfileByUserEmail")
    public PatientProfile getPatientProfileByUserEmail(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return patientProfileService.getPatientProfileByUserEmail(email);
    }

    @PutMapping("/updatePatientProfile")
    public ResponseEntity<PatientProfileResponse> updatePatientProfile(
            @Valid @RequestBody PatientProfileRequest patientProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        patientProfileService.updatePatientProfile(patientProfileRequest, email);

        PatientProfileResponse patientProfileResponse = new PatientProfileResponse();
        patientProfileResponse.setFirstName(patientProfileRequest.getFirstName());
        patientProfileResponse.setLastName(patientProfileRequest.getLastName());
        patientProfileResponse.setDateOfBirth(patientProfileRequest.getDateOfBirth());
        patientProfileResponse.setPhoneNumber(patientProfileRequest.getPhoneNumber());
        patientProfileResponse.setNicNumber(patientProfileRequest.getNicNumber());
        patientProfileResponse.setAddress(patientProfileRequest.getAddress());
        patientProfileResponse.setGender(patientProfileRequest.getGender());
        patientProfileResponse.setBloodGroup(patientProfileRequest.getBloodGroup());
        patientProfileResponse.setAllergies(patientProfileRequest.getAllergies());
        patientProfileResponse.setChronicDiseases(patientProfileRequest.getChronicDiseases());
        patientProfileResponse.setEmergencyContact(patientProfileRequest.getEmergencyContact());

        return ResponseEntity.ok(patientProfileResponse);
    }
}
