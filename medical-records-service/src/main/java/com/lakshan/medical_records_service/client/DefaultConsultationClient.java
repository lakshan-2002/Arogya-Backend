package com.lakshan.medical_records_service.client;

import org.springframework.stereotype.Component;

@Component
public class DefaultConsultationClient implements ConsultationClient {
    
    @Override
    public void updateLabTestStatus(Long id, StatusUpdateRequest request) {
        // Fallback: log error or handle gracefully
        System.err.println("Failed to update lab test status for ID: " + id);
    }
}
