package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.AdminProfile;
import com.lakshan.user_service.service.AdminProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AdminProfile> createAdminProfile(@RequestBody AdminProfile adminProfile) {
        adminProfileService.createNewAdminProfile(adminProfile);
        return ResponseEntity.status(201).body(adminProfile);
    }

    @GetMapping("/getAdminProfile/{id}")
    public AdminProfile getAdminProfile(@PathVariable int id) {
        return adminProfileService.getAdminProfileById(id);
    }

    @GetMapping("/getAllAdminProfiles")
    public List<AdminProfile> getAllAdminProfiles() {
        return adminProfileService.getAllAdminProfiles();
    }

    @GetMapping("/getAdminProfileByUserId/{userId}")
    public AdminProfile getAdminProfileByUserId(@PathVariable int userId) {
        return adminProfileService.getAdminProfileByUserId(userId);
    }

    @PutMapping("/updateAdminProfile")
    public ResponseEntity<AdminProfile> updateAdminProfile(@RequestBody AdminProfile adminProfile) {
        adminProfileService.updateAdminProfile(adminProfile);
        return ResponseEntity.ok(adminProfile);
    }

    @DeleteMapping("/deleteAdminProfile/{id}")
    public void deleteAdminProfile(@PathVariable int id) {
        adminProfileService.deleteAdminProfile(id);
    }
}
