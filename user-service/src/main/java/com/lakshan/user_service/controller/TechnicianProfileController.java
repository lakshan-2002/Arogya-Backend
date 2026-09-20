package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.TechnicianProfile;
import com.lakshan.user_service.service.TechnicianProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TechnicianProfile> createTechnicianProfile(@RequestBody TechnicianProfile technicianProfile) {
        technicianProfileService.createNewTechnicianProfile(technicianProfile);
        return ResponseEntity.status(201).body(technicianProfile);
    }

    @GetMapping("/getTechnicianProfile/{id}")
    public TechnicianProfile getTechnicianProfile(@PathVariable int id) {
        return technicianProfileService.getTechnicianProfileById(id);
    }

    @GetMapping("/getAllTechnicianProfiles")
    public List<TechnicianProfile> getAllTechnicianProfiles() {
        return technicianProfileService.getAllTechnicianProfiles();
    }

    @GetMapping("/getTechnicianProfileByUserId/{userId}")
    public TechnicianProfile getTechnicianProfileByUserId(@PathVariable int userId) {
        return technicianProfileService.getTechnicianProfileByUserId(userId);
    }

    @PutMapping("/updateTechnicianProfile")
    public ResponseEntity<TechnicianProfile> updateTechnicianProfile(@RequestBody TechnicianProfile technicianProfile) {
        technicianProfileService.updateTechnicianProfile(technicianProfile);
        return ResponseEntity.ok(technicianProfile);
    }

    @DeleteMapping("/deleteTechnicianProfile/{id}")
    public void deleteTechnicianProfile(@PathVariable int id) {
        technicianProfileService.deleteTechnicianProfile(id);
    }

}
