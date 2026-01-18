package com.lakshan.consultation_service.client;

import org.springframework.stereotype.Component;

@Component
public class DefaultUserClient implements UserClient {
    @Override
    public boolean userExists(Long userId) {
        // TODO: replace with Feign/WebClient call to User Service
        return true;
    }

    @Override
    public boolean doctorExists(Long doctorId) {
        // TODO: replace with Feign/WebClient call to User/Doctor Service
        return true;
    }
}
