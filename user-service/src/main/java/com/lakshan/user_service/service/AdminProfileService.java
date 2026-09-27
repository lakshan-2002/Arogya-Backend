package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.AdminProfile;
import com.lakshan.user_service.exceptions.AdminProfileNotFoundException;
import com.lakshan.user_service.exceptions.UserNotFoundException;
import com.lakshan.user_service.models.AdminProfileRequest;
import com.lakshan.user_service.repository.AdminProfileRepository;
import com.lakshan.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminProfileService {

    private final AdminProfileRepository adminProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminProfileService(AdminProfileRepository adminProfileRepository, UserRepository userRepository) {
        this.adminProfileRepository = adminProfileRepository;
        this.userRepository = userRepository;
    }

    public void createNewAdminProfile(AdminProfileRequest adminProfileRequest, String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        AdminProfile adminProfile = new AdminProfile();
        adminProfile.setFirstName(adminProfileRequest.getFirstName());
        adminProfile.setLastName(adminProfileRequest.getLastName());
        adminProfile.setDateOfBirth(adminProfileRequest.getDateOfBirth());
        adminProfile.setPhoneNumber(adminProfileRequest.getPhoneNumber());
        adminProfile.setNicNumber(adminProfileRequest.getNicNumber());
        adminProfile.setUser(user);

        adminProfileRepository.save(adminProfile);
    }

    public List<AdminProfile> getAllAdminProfiles() {
        return adminProfileRepository.findAll();
    }

    public AdminProfile getAdminProfileByUserEmail(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        return adminProfileRepository.findByUserId(user.getId());
    }

    public void updateAdminProfile(AdminProfileRequest adminProfileRequest, String email) {
        if (adminProfileRepository.existsById(adminProfileRequest.getId())) {
            var user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("User not found with email: " + email)
            );

            var adminProfile = adminProfileRepository.findByIdAndUserId(adminProfileRequest.getId(), user.getId())
                    .orElseThrow(() -> new AdminProfileNotFoundException(
                            "Admin profile not found for user with email: " + email)
                    );

            adminProfile.setFirstName(adminProfileRequest.getFirstName());
            adminProfile.setLastName(adminProfileRequest.getLastName());
            adminProfile.setDateOfBirth(adminProfileRequest.getDateOfBirth());
            adminProfile.setPhoneNumber(adminProfileRequest.getPhoneNumber());
            adminProfile.setNicNumber(adminProfileRequest.getNicNumber());
            adminProfile.setUser(user);

            adminProfileRepository.save(adminProfile);
        } else {
            throw new AdminProfileNotFoundException(
                    "Admin profile not found with id: " + adminProfileRequest.getId()
            );
        }


    }
}
