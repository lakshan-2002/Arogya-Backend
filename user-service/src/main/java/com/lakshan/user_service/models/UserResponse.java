package com.lakshan.user_service.models;

import com.lakshan.user_service.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {

    private String username;
    private String email;
    private UserRole userRole;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userRole=" + (userRole != null ? userRole.toString() : "No role") +
                '}';

    }
}
