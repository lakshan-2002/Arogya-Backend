package com.lakshan.clinic_service.service;


import com.lakshan.clinic_service.entity.ClinicDoctors;
import com.lakshan.clinic_service.repository.ClinicDoctorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicDoctorsService {

    private final ClinicDoctorsRepository clinicDoctorsRepository;

    @Autowired
    public ClinicDoctorsService(ClinicDoctorsRepository clinicDoctorsRepository) {
        this.clinicDoctorsRepository = clinicDoctorsRepository;
    }

    public List<ClinicDoctors> getAllClinicDoctors() {
        return clinicDoctorsRepository.findAll();
    }

    public ClinicDoctors getClinicDoctorById(int id) {
        return clinicDoctorsRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("ClinicDoctor not found with id: " + id));
    }

    public List<ClinicDoctors> getClinicDoctorsByClinicId(int clinicId) {
        if (clinicDoctorsRepository.findByClinicId(clinicId).isEmpty()) {
            throw new IllegalArgumentException("No doctors found for clinic id: " + clinicId);
        } else {
            return clinicDoctorsRepository.findByClinicId(clinicId);
        }
    }

}
