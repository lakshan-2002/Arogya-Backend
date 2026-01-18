package com.lakshan.consultation_service.client;

import org.springframework.stereotype.Component;

@Component
public class DefaultClinicClient implements ClinicClient {
    @Override
    public boolean clinicExists(Long clinicId) {
        // TODO: replace with Feign/WebClient call to Clinic Service
        return true;
    }
}
