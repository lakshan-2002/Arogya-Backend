package com.lakshan.medical_records_service.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = DefaultUserClient.class)
public interface UserClient {

    @GetMapping("/users/getUserByEmail/{email}")
    UserLookupResponse getUserByEmail(@PathVariable("email") String email);

    @JsonIgnoreProperties(ignoreUnknown = true)
    record UserLookupResponse(Long id) {}
}
