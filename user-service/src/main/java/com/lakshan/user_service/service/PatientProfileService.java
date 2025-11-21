package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.PatientProfile;
import com.lakshan.user_service.repository.PatientProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientProfileService {

    private final PatientProfileRepository patientProfileRepository;

    @Autowired
    public PatientProfileService(PatientProfileRepository patientProfileRepository) {
        this.patientProfileRepository = patientProfileRepository;
    }

    public void createNewPatientProfile(PatientProfile patientProfile) {
        patientProfileRepository.save(patientProfile);
    }

    public PatientProfile getPatientProfileById(int id) {
        return patientProfileRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Patient Profile not found with id: " + id)
        );
    }

    public List<PatientProfile> getAllPatientProfiles() {
        return patientProfileRepository.findAll();
    }

    public PatientProfile getPatientProfileByUserId(int userId) {
        return patientProfileRepository.findByUserId(userId).orElseThrow(() ->
                new IllegalArgumentException("Patient Profile not found with user id: " + userId)
        );
    }

    public void updatePatientProfile(PatientProfile patientProfile) {
        if (patientProfileRepository.existsById(patientProfile.getId()))
            patientProfileRepository.save(patientProfile);
        else
            throw new IllegalArgumentException("Patient Profile not found with id: " + patientProfile.getId());
    }

    public void deletePatientProfile(int id) {
        if (patientProfileRepository.existsById(id))
            patientProfileRepository.deleteById(id);
        else
            throw new IllegalArgumentException("Patient Profile not found with id: " + id);
    }
}
