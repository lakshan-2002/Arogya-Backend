package com.lakshan.medical_records_service.client;

import com.lakshan.medical_records_service.exception.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserClient implements UserClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultUserClient.class);

    @Override
    public UserLookupResponse getUserByEmail(String email) {
        log.error("user-service unreachable: failed to resolve user for email: {}", email);
        throw new ForbiddenException("Could not verify caller identity");
    }
}
