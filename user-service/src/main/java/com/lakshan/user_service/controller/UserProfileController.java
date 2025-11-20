package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.UserProfile;
import com.lakshan.user_service.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user_profiles")
@CrossOrigin(origins = "http://localhost:5173")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("/addUserProfile")
    public ResponseEntity<UserProfile> addUserProfile(@RequestBody UserProfile userProfile) {
        userProfileService.addNewUserProfile(userProfile);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/getUserProfile/{id}")
    public UserProfile getUserProfile(@PathVariable int id) {
        return userProfileService.getUserProfileById(id);
    }

    @GetMapping("/getAllUserProfiles")
    public List<UserProfile> getUserProfiles() {
        return userProfileService.getAllUserProfiles();
    }

    @GetMapping("/getUserProfileByUserId/{userId}")
    public UserProfile getUserProfileByUserId(@PathVariable int userId) {
        return userProfileService.getUserProfileByUserId(userId);
    }

    @PutMapping("/updateUserProfile")
    public ResponseEntity<UserProfile> updateUserProfile(@RequestBody UserProfile userProfile) {
        userProfileService.updateUserProfile(userProfile);
        return ResponseEntity.ok(userProfile);
    }

    @DeleteMapping("/deleteUserProfile/{id}")
    public void deleteUserProfile(@PathVariable int id) {
        userProfileService.deleteUserProfile(id);
    }
}
