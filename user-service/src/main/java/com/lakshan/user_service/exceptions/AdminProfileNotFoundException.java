package com.lakshan.user_service.exceptions;

public class AdminProfileNotFoundException extends RuntimeException {

    public AdminProfileNotFoundException(String message) {
        super(message);
    }
}
