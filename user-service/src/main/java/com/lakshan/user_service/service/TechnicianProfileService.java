package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.TechnicianProfile;
import com.lakshan.user_service.repository.TechnicianProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianProfileService {

    private final TechnicianProfileRepository technicianProfileRepository;

    @Autowired
    public TechnicianProfileService(TechnicianProfileRepository technicianProfileRepository) {
        this.technicianProfileRepository = technicianProfileRepository;
    }

    public void createNewTechnicianProfile(TechnicianProfile technicianProfile) {
        technicianProfileRepository.save(technicianProfile);
    }

    public TechnicianProfile getTechnicianProfileById(int id) {
        return technicianProfileRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Technician Profile not found with id: " + id)
        );
    }

    public List<TechnicianProfile> getAllTechnicianProfiles() {
        return technicianProfileRepository.findAll();
    }

    public TechnicianProfile getTechnicianProfileByUserId(int userId) {
        return technicianProfileRepository.findByUserId(userId).orElseThrow(() ->
                new IllegalArgumentException("Technician Profile not found with user id: " + userId)
        );
    }

    public void updateTechnicianProfile(TechnicianProfile technicianProfile) {
        if (technicianProfileRepository.existsById(technicianProfile.getId()))
            technicianProfileRepository.save(technicianProfile);
        else
            throw new IllegalArgumentException("Technician Profile not found with id: " + technicianProfile.getId());
    }

    public void deleteTechnicianProfile(int id) {
        if (technicianProfileRepository.existsById(id))
            technicianProfileRepository.deleteById(id);
        else
            throw new IllegalArgumentException("Technician Profile not found with id: " + id);
    }


}
