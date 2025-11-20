package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.User;
import com.lakshan.user_service.entity.UserRole;
import com.lakshan.user_service.models.UserRequest;
import com.lakshan.user_service.repository.UserRepository;
import com.lakshan.user_service.repository.UserRoleRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public void addNewUser(UserRequest  userRequest) {
        UserRole userRole = userRoleRepository.findById(userRequest.getUserRole().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Role not found with id: " + userRequest.getUserRole().getId()
                ));

        if(userRole.getSecretKey() != null && !(userRole.getSecretKey().equals(userRequest.getSecretKey()))) {
            throw new IllegalArgumentException("Invalid secret key for role: " + userRole.getRoleName());
        }
        else {
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(DigestUtils.sha256Hex(userRequest.getPassword()));
            user.setUserRole(userRole);
            userRepository.save(user);
        }

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with id: " + id)
        );
    }

    public void updateUser(UserRequest userRequest) {
        if(userRepository.existsById(userRequest.getId())) {
            User user = new User();
            user.setId(userRequest.getId());
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(DigestUtils.sha256Hex(userRequest.getPassword()));
            user.setUserRole(userRequest.getUserRole());
            userRepository.save(user);
        }
        else {
            throw new IllegalArgumentException("User not found with id: " + userRequest.getId());
        }
    }

    public void deleteUser(int id) {
        if (userRepository.existsById(id))
            userRepository.deleteById(id);
        else
            throw new IllegalArgumentException("User not found with id: " + id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("User not found with email: " + email)
        );

    }
}
