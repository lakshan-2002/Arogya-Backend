package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.PatientProfile;
import com.lakshan.user_service.exceptions.PatientProfileNotFoundException;
import com.lakshan.user_service.exceptions.UserNotFoundException;
import com.lakshan.user_service.models.PatientProfileRequest;
import com.lakshan.user_service.repository.PatientProfileRepository;
import com.lakshan.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientProfileService {

    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public PatientProfileService(PatientProfileRepository patientProfileRepository, UserRepository userRepository) {
        this.patientProfileRepository = patientProfileRepository;
        this.userRepository = userRepository;
    }

    public void createNewPatientProfile(PatientProfileRequest patientProfileRequest, String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setFirstName(patientProfileRequest.getFirstName());
        patientProfile.setLastName(patientProfileRequest.getLastName());
        patientProfile.setDateOfBirth(patientProfileRequest.getDateOfBirth());
        patientProfile.setPhoneNumber(patientProfileRequest.getPhoneNumber());
        patientProfile.setNicNumber(patientProfileRequest.getNicNumber());
        patientProfile.setAddress(patientProfileRequest.getAddress());
        patientProfile.setGender(patientProfileRequest.getGender());
        patientProfile.setBloodGroup(patientProfileRequest.getBloodGroup());
        patientProfile.setAllergies(patientProfileRequest.getAllergies());
        patientProfile.setChronicDiseases(patientProfileRequest.getChronicDiseases());
        patientProfile.setEmergencyContact(patientProfileRequest.getEmergencyContact());
        patientProfile.setUser(user);

        patientProfileRepository.save(patientProfile);
    }

    public List<PatientProfile> getAllPatientProfiles() {
        return patientProfileRepository.findAll();
    }

    public PatientProfile getPatientProfileByUserEmail(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        return patientProfileRepository.findByUserId(user.getId());
    }

    public void updatePatientProfile(PatientProfileRequest patientProfileRequest, String email) {
        if (patientProfileRepository.existsById(patientProfileRequest.getId())) {
            var user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("User not found with email: " + email)
            );

            var patientProfile = patientProfileRepository.findByIdAndUserId(patientProfileRequest.getId(), user.getId())
                    .orElseThrow(() -> new PatientProfileNotFoundException(
                            "Patient profile not found for user with email: " + email)
                    );

            patientProfile.setFirstName(patientProfileRequest.getFirstName());
            patientProfile.setLastName(patientProfileRequest.getLastName());
            patientProfile.setDateOfBirth(patientProfileRequest.getDateOfBirth());
            patientProfile.setPhoneNumber(patientProfileRequest.getPhoneNumber());
            patientProfile.setNicNumber(patientProfileRequest.getNicNumber());
            patientProfile.setAddress(patientProfileRequest.getAddress());
            patientProfile.setGender(patientProfileRequest.getGender());
            patientProfile.setBloodGroup(patientProfileRequest.getBloodGroup());
            patientProfile.setAllergies(patientProfileRequest.getAllergies());
            patientProfile.setChronicDiseases(patientProfileRequest.getChronicDiseases());
            patientProfile.setEmergencyContact(patientProfileRequest.getEmergencyContact());
            patientProfile.setUser(user);

            patientProfileRepository.save(patientProfile);
        } else {
            throw new PatientProfileNotFoundException(
                    "Patient profile not found with id: " + patientProfileRequest.getId()
            );
        }
    }
}
