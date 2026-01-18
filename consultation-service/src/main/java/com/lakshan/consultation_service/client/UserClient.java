package com.lakshan.consultation_service.client;

public interface UserClient {
    boolean userExists(Long userId);
    boolean doctorExists(Long doctorId);
}
