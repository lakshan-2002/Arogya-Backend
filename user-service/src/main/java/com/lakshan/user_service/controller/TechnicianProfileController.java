package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.TechnicianProfile;
import com.lakshan.user_service.models.TechnicianProfileRequest;
import com.lakshan.user_service.models.TechnicianProfileResponse;
import com.lakshan.user_service.service.TechnicianProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/technician_profile")
public class TechnicianProfileController {

    private final TechnicianProfileService technicianProfileService;

    @Autowired
    public TechnicianProfileController(TechnicianProfileService technicianProfileService) {
        this.technicianProfileService = technicianProfileService;
    }

    @PostMapping("/createTechnicianProfile")
    public ResponseEntity<TechnicianProfileResponse> createTechnicianProfile(
            @Valid @RequestBody TechnicianProfileRequest technicianProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        technicianProfileService.createNewTechnicianProfile(technicianProfileRequest, email);

        TechnicianProfileResponse technicianProfileResponse = new TechnicianProfileResponse();
        technicianProfileResponse.setFirstName(technicianProfileRequest.getFirstName());
        technicianProfileResponse.setLastName(technicianProfileRequest.getLastName());
        technicianProfileResponse.setDateOfBirth(technicianProfileRequest.getDateOfBirth());
        technicianProfileResponse.setPhoneNumber(technicianProfileRequest.getPhoneNumber());
        technicianProfileResponse.setNicNumber(technicianProfileRequest.getNicNumber());
        technicianProfileResponse.setTechnicianField(technicianProfileRequest.getTechnicianField());
        technicianProfileResponse.setLicenseNumber(technicianProfileRequest.getLicenseNumber());
        technicianProfileResponse.setCertification(technicianProfileRequest.getCertification());
        technicianProfileResponse.setAssignedEquipment(technicianProfileRequest.getAssignedEquipment());

        return ResponseEntity.status(201).body(technicianProfileResponse);
    }

    @GetMapping("/getAllTechnicianProfiles")
    public List<TechnicianProfile> getAllTechnicianProfiles() {
        return technicianProfileService.getAllTechnicianProfiles();
    }

    @GetMapping("/getTechnicianProfileByUserId/{userId}")
    public TechnicianProfile getTechnicianProfileByUserId(@PathVariable int userId) {
        return technicianProfileService.getTechnicianProfileByUserId(userId);
    }

    @GetMapping("/getTechnicianProfileByUserEmail")
    public TechnicianProfile getTechnicianProfileByUserEmail(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return technicianProfileService.getTechnicianProfileByUserEmail(email);
    }

    @PutMapping("/updateTechnicianProfile")
    public ResponseEntity<TechnicianProfileResponse> updateTechnicianProfile(
            @Valid @RequestBody TechnicianProfileRequest technicianProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        technicianProfileService.updateTechnicianProfile(technicianProfileRequest, email);

        TechnicianProfileResponse technicianProfileResponse = new TechnicianProfileResponse();
        technicianProfileResponse.setFirstName(technicianProfileRequest.getFirstName());
        technicianProfileResponse.setLastName(technicianProfileRequest.getLastName());
        technicianProfileResponse.setDateOfBirth(technicianProfileRequest.getDateOfBirth());
        technicianProfileResponse.setPhoneNumber(technicianProfileRequest.getPhoneNumber());
        technicianProfileResponse.setNicNumber(technicianProfileRequest.getNicNumber());
        technicianProfileResponse.setTechnicianField(technicianProfileRequest.getTechnicianField());
        technicianProfileResponse.setLicenseNumber(technicianProfileRequest.getLicenseNumber());
        technicianProfileResponse.setCertification(technicianProfileRequest.getCertification());
        technicianProfileResponse.setAssignedEquipment(technicianProfileRequest.getAssignedEquipment());

        return ResponseEntity.ok(technicianProfileResponse);
    }
}
