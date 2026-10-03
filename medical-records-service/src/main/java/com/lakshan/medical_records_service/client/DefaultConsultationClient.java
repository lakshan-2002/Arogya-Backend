package com.lakshan.medical_records_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultConsultationClient implements ConsultationClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultConsultationClient.class);

    @Override
    public void updateLabTestStatus(Long id, StatusUpdateRequest request) {
        log.error("consultation-service unreachable: failed to update lab test status for ID: {}", id);
    }

    @Override
    public void startLabTest(Long id) {
        log.error("consultation-service unreachable: failed to start lab test for ID: {}", id);
    }
}
