package com.lakshan.queue_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CLINIC-SERVICE")
public interface ClinicServiceClient {

    @GetMapping("/clinics/getClinic/{id}")
    Object getClinicById(@PathVariable("id") int id);
}
