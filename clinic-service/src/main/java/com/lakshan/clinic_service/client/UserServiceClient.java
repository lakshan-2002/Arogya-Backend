package com.lakshan.clinic_service.client;

import com.lakshan.clinic_service.model.DoctorProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserServiceClient {

    @GetMapping("/doctor_profile/getDoctorProfile/{id}")
    DoctorProfileDTO getDoctorProfile(@PathVariable("id") int id);

}
