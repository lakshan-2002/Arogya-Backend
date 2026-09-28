package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.DoctorProfile;
import com.lakshan.user_service.exceptions.DoctorProfileNotFoundException;
import com.lakshan.user_service.exceptions.UserNotFoundException;
import com.lakshan.user_service.models.DoctorProfileRequest;
import com.lakshan.user_service.repository.DoctorProfileRepository;
import com.lakshan.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public DoctorProfileService(DoctorProfileRepository doctorProfileRepository, UserRepository userRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
    }

    public void createNewDoctorProfile(DoctorProfileRequest doctorProfileRequest, String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.setFirstName(doctorProfileRequest.getFirstName());
        doctorProfile.setLastName(doctorProfileRequest.getLastName());
        doctorProfile.setDateOfBirth(doctorProfileRequest.getDateOfBirth());
        doctorProfile.setPhoneNumber(doctorProfileRequest.getPhoneNumber());
        doctorProfile.setNicNumber(doctorProfileRequest.getNicNumber());
        doctorProfile.setLicenseNumber(doctorProfileRequest.getLicenseNumber());
        doctorProfile.setSpecialization(doctorProfileRequest.getSpecialization());
        doctorProfile.setQualification(doctorProfileRequest.getQualification());
        doctorProfile.setExperienceYears(doctorProfileRequest.getExperienceYears());
        doctorProfile.setUser(user);

        doctorProfileRepository.save(doctorProfile);
    }

    public List<DoctorProfile> getAllDoctorProfiles() {
        return doctorProfileRepository.findAll();
    }

    public List<DoctorProfile> getDoctorProfilesByIds(List<Integer> ids) {
        return doctorProfileRepository.findAllById(ids);
    }

    public DoctorProfile getDoctorProfileByUserId(int userId) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(userId);
        if (profile == null) {
            throw new DoctorProfileNotFoundException("Doctor profile not found for user id: " + userId);
        }
        return profile;
    }

    public DoctorProfile getDoctorProfileByUserEmail(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );

        DoctorProfile profile = doctorProfileRepository.findByUserId(user.getId());
        if (profile == null) {
            throw new DoctorProfileNotFoundException("Doctor profile not found for user with email: " + email);
        }
        return profile;
    }

    public void updateDoctorProfile(DoctorProfileRequest doctorProfileRequest, String email) {
        if (doctorProfileRepository.existsById(doctorProfileRequest.getId())) {
            var user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("User not found with email: " + email)
            );

            var doctorProfile = doctorProfileRepository.findByIdAndUserId(doctorProfileRequest.getId(), user.getId())
                    .orElseThrow(() -> new DoctorProfileNotFoundException(
                            "Doctor profile not found for user with email: " + email)
                    );

            doctorProfile.setFirstName(doctorProfileRequest.getFirstName());
            doctorProfile.setLastName(doctorProfileRequest.getLastName());
            doctorProfile.setDateOfBirth(doctorProfileRequest.getDateOfBirth());
            doctorProfile.setPhoneNumber(doctorProfileRequest.getPhoneNumber());
            doctorProfile.setNicNumber(doctorProfileRequest.getNicNumber());
            doctorProfile.setLicenseNumber(doctorProfileRequest.getLicenseNumber());
            doctorProfile.setSpecialization(doctorProfileRequest.getSpecialization());
            doctorProfile.setQualification(doctorProfileRequest.getQualification());
            doctorProfile.setExperienceYears(doctorProfileRequest.getExperienceYears());
            doctorProfile.setUser(user);

            doctorProfileRepository.save(doctorProfile);
        } else {
            throw new DoctorProfileNotFoundException(
                    "Doctor profile not found with id: " + doctorProfileRequest.getId()
            );
        }
    }
}
