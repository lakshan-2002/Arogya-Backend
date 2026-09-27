package com.lakshan.user_service.exceptions;

public class DoctorProfileNotFoundException extends RuntimeException {

    public DoctorProfileNotFoundException(String message) {
        super(message);
    }
}
