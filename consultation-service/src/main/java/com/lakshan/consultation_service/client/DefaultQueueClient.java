package com.lakshan.consultation_service.client;

import org.springframework.stereotype.Component;

@Component
public class DefaultQueueClient implements QueueClient {
    @Override
    public boolean queueTokenExists(Long queueTokenId) {
        // TODO: replace with Feign/WebClient call to Queue Service
        return true;
    }
}
