package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.TechnicianProfile;
import com.lakshan.user_service.exceptions.TechnicianProfileNotFoundException;
import com.lakshan.user_service.exceptions.UserNotFoundException;
import com.lakshan.user_service.models.TechnicianProfileRequest;
import com.lakshan.user_service.repository.TechnicianProfileRepository;
import com.lakshan.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianProfileService {

    private final TechnicianProfileRepository technicianProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public TechnicianProfileService(TechnicianProfileRepository technicianProfileRepository, UserRepository userRepository) {
        this.technicianProfileRepository = technicianProfileRepository;
        this.userRepository = userRepository;
    }

    public void createNewTechnicianProfile(TechnicianProfileRequest technicianProfileRequest, String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        TechnicianProfile technicianProfile = new TechnicianProfile();
        technicianProfile.setFirstName(technicianProfileRequest.getFirstName());
        technicianProfile.setLastName(technicianProfileRequest.getLastName());
        technicianProfile.setDateOfBirth(technicianProfileRequest.getDateOfBirth());
        technicianProfile.setPhoneNumber(technicianProfileRequest.getPhoneNumber());
        technicianProfile.setNicNumber(technicianProfileRequest.getNicNumber());
        technicianProfile.setTechnicianField(technicianProfileRequest.getTechnicianField());
        technicianProfile.setLicenseNumber(technicianProfileRequest.getLicenseNumber());
        technicianProfile.setCertification(technicianProfileRequest.getCertification());
        technicianProfile.setAssignedEquipment(technicianProfileRequest.getAssignedEquipment());
        technicianProfile.setUser(user);

        technicianProfileRepository.save(technicianProfile);
    }

    public List<TechnicianProfile> getAllTechnicianProfiles() {
        return technicianProfileRepository.findAll();
    }

    public TechnicianProfile getTechnicianProfileByUserId(int userId) {
        TechnicianProfile profile = technicianProfileRepository.findByUserId(userId);
        if (profile == null) {
            throw new TechnicianProfileNotFoundException("Technician profile not found for user id: " + userId);
        }
        return profile;
    }

    public TechnicianProfile getTechnicianProfileByUserEmail(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        TechnicianProfile profile = technicianProfileRepository.findByUserId(user.getId());
        if (profile == null) {
            throw new TechnicianProfileNotFoundException("Technician profile not found for user with email: " + email);
        }
        return profile;
    }

    public void updateTechnicianProfile(TechnicianProfileRequest technicianProfileRequest, String email) {
        if (technicianProfileRepository.existsById(technicianProfileRequest.getId())) {
            var user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("User not found with email: " + email)
            );

            var technicianProfile = technicianProfileRepository.findByIdAndUserId(technicianProfileRequest.getId(), user.getId())
                    .orElseThrow(() -> new TechnicianProfileNotFoundException(
                            "Technician profile not found for user with email: " + email)
                    );

            technicianProfile.setFirstName(technicianProfileRequest.getFirstName());
            technicianProfile.setLastName(technicianProfileRequest.getLastName());
            technicianProfile.setDateOfBirth(technicianProfileRequest.getDateOfBirth());
            technicianProfile.setPhoneNumber(technicianProfileRequest.getPhoneNumber());
            technicianProfile.setNicNumber(technicianProfileRequest.getNicNumber());
            technicianProfile.setTechnicianField(technicianProfileRequest.getTechnicianField());
            technicianProfile.setLicenseNumber(technicianProfileRequest.getLicenseNumber());
            technicianProfile.setCertification(technicianProfileRequest.getCertification());
            technicianProfile.setAssignedEquipment(technicianProfileRequest.getAssignedEquipment());
            technicianProfile.setUser(user);

            technicianProfileRepository.save(technicianProfile);
        } else {
            throw new TechnicianProfileNotFoundException(
                    "Technician profile not found with id: " + technicianProfileRequest.getId()
            );
        }
    }
}
