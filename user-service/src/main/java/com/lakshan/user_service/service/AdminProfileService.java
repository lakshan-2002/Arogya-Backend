package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.AdminProfile;
import com.lakshan.user_service.repository.AdminProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminProfileService {

    private final AdminProfileRepository adminProfileRepository;

    @Autowired
    public AdminProfileService(AdminProfileRepository adminProfileRepository) {
        this.adminProfileRepository = adminProfileRepository;
    }

    public void createNewAdminProfile(AdminProfile adminProfile) {
        adminProfileRepository.save(adminProfile);
    }

    public AdminProfile getAdminProfileById(int id) {
        return adminProfileRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Admin Profile not found with id: " + id)
        );
    }

    public List<AdminProfile> getAllAdminProfiles() {
        return adminProfileRepository.findAll();
    }

    public AdminProfile getAdminProfileByUserId(int userId) {
        return adminProfileRepository.findByUserId(userId).orElseThrow(() ->
                new IllegalArgumentException("Admin Profile not found with user id: " + userId)
        );
    }

    public void updateAdminProfile(AdminProfile adminProfile) {
        if (adminProfileRepository.existsById(adminProfile.getId()))
            adminProfileRepository.save(adminProfile);
        else
            throw new IllegalArgumentException("Admin Profile not found with id: " + adminProfile.getId());
    }

    public void deleteAdminProfile(int id) {
        if (adminProfileRepository.existsById(id))
            adminProfileRepository.deleteById(id);
        else
            throw new IllegalArgumentException("Admin Profile not found with id: " + id);
    }
}
