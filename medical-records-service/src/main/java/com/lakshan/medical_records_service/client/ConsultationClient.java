package com.lakshan.medical_records_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "consultation-service", fallback = DefaultConsultationClient.class)
public interface ConsultationClient {

    @PutMapping("/lab-tests/{id}/technician-update")
    void updateLabTestStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request);

    @PostMapping("/lab-tests/{id}/start")
    void startLabTest(@PathVariable Long id);

    record StatusUpdateRequest(String status, String testResults, String technicianNotes) {}
}
