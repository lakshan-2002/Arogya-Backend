package com.lakshan.clinic_service.service;

import com.lakshan.clinic_service.client.UserServiceClient;
import com.lakshan.clinic_service.entity.Clinic;
import com.lakshan.clinic_service.entity.ClinicDoctors;
import com.lakshan.clinic_service.model.ClinicRequest;
import com.lakshan.clinic_service.repository.ClinicDoctorsRepository;
import com.lakshan.clinic_service.repository.ClinicRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final UserServiceClient userServiceClient;
    private final ClinicDoctorsRepository clinicDoctorsRepository;

    @Autowired
    public ClinicService(ClinicRepository clinicRepository,
                         UserServiceClient userServiceClient,
                         ClinicDoctorsRepository clinicDoctorsRepository
    ) {
        this.clinicRepository = clinicRepository;
        this.userServiceClient = userServiceClient;
        this.clinicDoctorsRepository = clinicDoctorsRepository;
    }

    @Transactional
    public void createClinic(ClinicRequest clinicRequest) {
        Clinic clinic = new Clinic();
        clinic.setClinicName(clinicRequest.getClinicName());
        clinic.setProvince(clinicRequest.getProvince());
        clinic.setDistrict(clinicRequest.getDistrict());
        clinic.setLocation(clinicRequest.getLocation());
        clinic.setScheduledDate(clinicRequest.getScheduledDate());
        clinic.setScheduledTime(clinicRequest.getScheduledTime());
        clinic.setStatus(clinicRequest.getStatus());

        clinicRepository.save(clinic);

        for (Integer doctorIds : clinicRequest.getDoctorIds()) {
            var doctorProfileDTO = userServiceClient.getDoctorProfile(doctorIds);

            ClinicDoctors clinicDoctors = new ClinicDoctors();
            clinicDoctors.setClinic(clinic);
            clinicDoctors.setDoctorRefId(doctorIds);
            clinicDoctors.setDoctorName(doctorProfileDTO.getFirstName());
            clinicDoctors.setSpecialization(doctorProfileDTO.getSpecialization());

            clinicDoctorsRepository.save(clinicDoctors);
        }
    }

    public Clinic getClinicById(int id) {
        return clinicRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Clinic not found with id: " + id));
    }

    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    @Transactional
    public void updateClinic(ClinicRequest clinicRequest) {
        if (clinicRepository.existsById(clinicRequest.getId())) {
            Clinic clinic = getClinic(clinicRequest);
            clinicRepository.save(clinic);

            clinicDoctorsRepository.deleteByClinicId(clinicRequest.getId());

            for (Integer doctorIds : clinicRequest.getDoctorIds()) {
                var doctorProfileDTO = userServiceClient.getDoctorProfile(doctorIds);

                ClinicDoctors clinicDoctors = new ClinicDoctors();
                clinicDoctors.setClinic(clinic);
                clinicDoctors.setDoctorRefId(doctorIds);
                clinicDoctors.setDoctorName(doctorProfileDTO.getFirstName());
                clinicDoctors.setSpecialization(doctorProfileDTO.getSpecialization());

                clinicDoctorsRepository.save(clinicDoctors);

            }
        } else {
            throw new IllegalArgumentException("Clinic not found with id: " + clinicRequest.getId());
        }
    }

    private static Clinic getClinic(ClinicRequest clinicRequest) {
        Clinic clinic = new Clinic();
        clinic.setId(clinicRequest.getId());
        clinic.setClinicName(clinicRequest.getClinicName());
        clinic.setProvince(clinicRequest.getProvince());
        clinic.setDistrict(clinicRequest.getDistrict());
        clinic.setLocation(clinicRequest.getLocation());
        clinic.setScheduledDate(clinicRequest.getScheduledDate());
        clinic.setScheduledTime(clinicRequest.getScheduledTime());
        clinic.setStatus(clinicRequest.getStatus());
        return clinic;
    }

    public void deleteClinic(int id) {
        if (clinicRepository.existsById(id)) {
            clinicRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Clinic not found with id: " + id);
        }
    }
}
