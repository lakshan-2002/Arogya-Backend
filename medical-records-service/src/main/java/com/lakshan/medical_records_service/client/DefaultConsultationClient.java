package com.lakshan.medical_records_service.client;

import org.springframework.stereotype.Component;

@Component
public class DefaultConsultationClient implements ConsultationClient {
    
    @Override
    public void updateLabTestStatus(Long id, StatusUpdateRequest request) {
        System.err.println("Failed to update lab test status for ID: " + id);
    }

    @Override
    public void startLabTest(Long id) {
        System.err.println("Failed to start lab test for ID: " + id);
    }
}
