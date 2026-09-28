package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.AdminProfile;
import com.lakshan.user_service.models.AdminProfileRequest;
import com.lakshan.user_service.models.AdminProfileResponse;
import com.lakshan.user_service.service.AdminProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin_profile")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @Autowired
    public AdminProfileController(AdminProfileService adminProfileService) {
        this.adminProfileService = adminProfileService;
    }

    @PostMapping("/createAdminProfile")
    public ResponseEntity<AdminProfileResponse> createAdminProfile(
            @Valid @RequestBody AdminProfileRequest adminProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        adminProfileService.createNewAdminProfile(adminProfileRequest, email);

        AdminProfileResponse adminProfileResponse = new AdminProfileResponse();
        adminProfileResponse.setFirstName(adminProfileRequest.getFirstName());
        adminProfileResponse.setLastName(adminProfileRequest.getLastName());
        adminProfileResponse.setDateOfBirth(adminProfileRequest.getDateOfBirth());
        adminProfileResponse.setPhoneNumber(adminProfileRequest.getPhoneNumber());
        adminProfileResponse.setNicNumber(adminProfileRequest.getNicNumber());

        return ResponseEntity.status(201).body(adminProfileResponse);
    }

    @GetMapping("/getAllAdminProfiles")
    public List<AdminProfile> getAllAdminProfiles() {
        return adminProfileService.getAllAdminProfiles();
    }

    @GetMapping("/getAdminProfileByUserId/{userId}")
    public AdminProfile getAdminProfileByUserId(@PathVariable int userId) {
        return adminProfileService.getAdminProfileByUserId(userId);
    }

    @GetMapping("/getAdminProfileByUserEmail")
    public AdminProfile getAdminProfileByUserEmail(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return adminProfileService.getAdminProfileByUserEmail(email);
    }

    @PutMapping("/updateAdminProfile")
    public ResponseEntity<AdminProfileResponse> updateAdminProfile(
            @Valid @RequestBody AdminProfileRequest adminProfileRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        adminProfileService.updateAdminProfile(adminProfileRequest, email);

        AdminProfileResponse adminProfileResponse = new AdminProfileResponse();
        adminProfileResponse.setFirstName(adminProfileRequest.getFirstName());
        adminProfileResponse.setLastName(adminProfileRequest.getLastName());
        adminProfileResponse.setDateOfBirth(adminProfileRequest.getDateOfBirth());
        adminProfileResponse.setPhoneNumber(adminProfileRequest.getPhoneNumber());
        adminProfileResponse.setNicNumber(adminProfileRequest.getNicNumber());

        return ResponseEntity.ok(adminProfileResponse);
    }
}
