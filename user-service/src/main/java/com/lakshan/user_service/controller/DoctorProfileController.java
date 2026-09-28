package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.DoctorProfile;
import com.lakshan.user_service.models.DoctorProfileRequest;
import com.lakshan.user_service.models.DoctorProfileResponse;
import com.lakshan.user_service.service.DoctorProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<DoctorProfileResponse> createDoctorProfile(
            @Valid @RequestBody DoctorProfileRequest doctorProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        doctorProfileService.createNewDoctorProfile(doctorProfileRequest, email);

        DoctorProfileResponse doctorProfileResponse = new DoctorProfileResponse();
        doctorProfileResponse.setFirstName(doctorProfileRequest.getFirstName());
        doctorProfileResponse.setLastName(doctorProfileRequest.getLastName());
        doctorProfileResponse.setDateOfBirth(doctorProfileRequest.getDateOfBirth());
        doctorProfileResponse.setPhoneNumber(doctorProfileRequest.getPhoneNumber());
        doctorProfileResponse.setNicNumber(doctorProfileRequest.getNicNumber());
        doctorProfileResponse.setLicenseNumber(doctorProfileRequest.getLicenseNumber());
        doctorProfileResponse.setSpecialization(doctorProfileRequest.getSpecialization());
        doctorProfileResponse.setQualification(doctorProfileRequest.getQualification());
        doctorProfileResponse.setExperienceYears(doctorProfileRequest.getExperienceYears());

        return ResponseEntity.status(201).body(doctorProfileResponse);
    }

    @PostMapping("/bulk")
    public List<DoctorProfile> getDoctorProfilesByIds(@RequestBody List<Integer> ids) {
        return doctorProfileService.getDoctorProfilesByIds(ids);
    }

    @GetMapping("/getAllDoctorProfiles")
    public List<DoctorProfile> getAllDoctorProfiles() {
        return doctorProfileService.getAllDoctorProfiles();
    }

    @GetMapping("/getDoctorProfileByUserId/{userId}")
    public DoctorProfile getDoctorProfileByUserId(@PathVariable int userId) {
        return doctorProfileService.getDoctorProfileByUserId(userId);
    }

    @GetMapping("/getDoctorProfileByUserEmail")
    public DoctorProfile getDoctorProfileByUserEmail(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return doctorProfileService.getDoctorProfileByUserEmail(email);
    }

    @PutMapping("/updateDoctorProfile")
    public ResponseEntity<DoctorProfileResponse> updateDoctorProfile(
            @Valid @RequestBody DoctorProfileRequest doctorProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        doctorProfileService.updateDoctorProfile(doctorProfileRequest, email);

        DoctorProfileResponse doctorProfileResponse = new DoctorProfileResponse();
        doctorProfileResponse.setFirstName(doctorProfileRequest.getFirstName());
        doctorProfileResponse.setLastName(doctorProfileRequest.getLastName());
        doctorProfileResponse.setDateOfBirth(doctorProfileRequest.getDateOfBirth());
        doctorProfileResponse.setPhoneNumber(doctorProfileRequest.getPhoneNumber());
        doctorProfileResponse.setNicNumber(doctorProfileRequest.getNicNumber());
        doctorProfileResponse.setLicenseNumber(doctorProfileRequest.getLicenseNumber());
        doctorProfileResponse.setSpecialization(doctorProfileRequest.getSpecialization());
        doctorProfileResponse.setQualification(doctorProfileRequest.getQualification());
        doctorProfileResponse.setExperienceYears(doctorProfileRequest.getExperienceYears());

        return ResponseEntity.ok(doctorProfileResponse);
    }
}
