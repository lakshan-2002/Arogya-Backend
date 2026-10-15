package com.lakshan.user_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;

    public static ApiResponse ok(String message) {
        return new ApiResponse(message, true);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(message, false);
    }
}
