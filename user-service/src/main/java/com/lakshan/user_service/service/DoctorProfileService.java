package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.DoctorProfile;
import com.lakshan.user_service.repository.DoctorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;

    @Autowired
    public DoctorProfileService(DoctorProfileRepository doctorProfileRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
    }

    public void createNewDoctorProfile(DoctorProfile doctorProfile) {
        doctorProfileRepository.save(doctorProfile);
    }

    public List<DoctorProfile> getAllDoctorProfiles() {
        return doctorProfileRepository.findAll();
    }

    public DoctorProfile getDoctorProfileById(int id) {
        return doctorProfileRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Doctor Profile not found with id: " + id)
        );
    }

    public DoctorProfile getDoctorProfileByUserId(int userId) {
        return doctorProfileRepository.findByUserId(userId).orElseThrow(() ->
                new IllegalArgumentException("Doctor Profile not found with user id: " + userId)
        );
    }

    public void updateDoctorProfile(DoctorProfile doctorProfile) {
        if (doctorProfileRepository.existsById(doctorProfile.getId()))
            doctorProfileRepository.save(doctorProfile);
        else
            throw new IllegalArgumentException("Doctor Profile not found with id: " + doctorProfile.getId());
    }

    public void deleteDoctorProfile(int id) {
        if (doctorProfileRepository.existsById(id))
            doctorProfileRepository.deleteById(id);
        else
            throw new IllegalArgumentException("Doctor Profile not found with id: " + id);
    }
}
