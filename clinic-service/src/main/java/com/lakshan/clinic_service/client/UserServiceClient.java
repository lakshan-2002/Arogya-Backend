package com.lakshan.clinic_service.client;

import com.lakshan.clinic_service.model.DoctorProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @PostMapping("/doctor_profile/bulk")
    List<DoctorProfileDTO> getDoctorProfiles(@RequestBody List<Integer> ids);

}
