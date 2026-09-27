package com.lakshan.user_service.exceptions;

public class PatientProfileNotFoundException extends RuntimeException {

    public PatientProfileNotFoundException(String message) {
        super(message);
    }
}
