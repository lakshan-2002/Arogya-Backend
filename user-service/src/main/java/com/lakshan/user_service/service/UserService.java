package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.User;
import com.lakshan.user_service.exceptions.DuplicateResourceException;
import com.lakshan.user_service.exceptions.InvalidKeyException;
import com.lakshan.user_service.exceptions.UserNotFoundException;
import com.lakshan.user_service.models.UserRequest;
import com.lakshan.user_service.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addNewUser(UserRequest userRequest) {
        if (userRequest
                .getUserRole()
                .getSecretKey() != null
                &&
                !(userRequest
                        .getUserRole()
                        .getSecretKey()
                        .equals(userRequest.getSecretKey())
                )
        ) {
            throw new InvalidKeyException("Invalid secret key for role: " + userRequest
                    .getUserRole()
                    .getRoleName()
            );
        }
        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()){
            throw new DuplicateResourceException("User already exists with email: " + userRequest.getEmail());
        }
        else {
            User user = new User();
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user.setUserRole(userRequest.getUserRole());
            userRepository.save(user);
        }
    }

    public void updateUser(UserRequest userRequest) {
        if (userRepository.existsById(userRequest.getId())) {
            User user = new User();
            user.setId(userRequest.getId());
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setPassword(DigestUtils.sha256Hex(userRequest.getPassword()));
            user.setUserRole(userRequest.getUserRole());
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User not found with id: " + userRequest.getId());
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email)
        );
    }
}
