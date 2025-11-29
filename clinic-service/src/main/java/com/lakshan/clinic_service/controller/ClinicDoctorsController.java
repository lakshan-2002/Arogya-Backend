package com.lakshan.clinic_service.controller;

import com.lakshan.clinic_service.entity.ClinicDoctors;
import com.lakshan.clinic_service.service.ClinicDoctorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clinic_doctors")
public class ClinicDoctorsController {

    private final ClinicDoctorsService clinicDoctorsService;

    @Autowired
    public ClinicDoctorsController(ClinicDoctorsService clinicDoctorsService) {
        this.clinicDoctorsService = clinicDoctorsService;
    }

    @GetMapping("/getAllClinicDoctors")
    public List<ClinicDoctors> getAllClinicDoctors() {
        return clinicDoctorsService.getAllClinicDoctors();
    }

    @GetMapping("/getClinicDoctor/{id}")
    public ClinicDoctors getClinicDoctorById(@PathVariable int id) {
        return clinicDoctorsService.getClinicDoctorById(id);
    }

    @GetMapping("/getClinicDoctorsByClinicId/{clinicId}")
    public List<ClinicDoctors> getClinicDoctorsByClinicId(@PathVariable int clinicId) {
        return clinicDoctorsService.getClinicDoctorsByClinicId(clinicId);
    }
}
