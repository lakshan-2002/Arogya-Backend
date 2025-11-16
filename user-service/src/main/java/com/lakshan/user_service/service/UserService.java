package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.User;
import com.lakshan.user_service.entity.UserRole;
import com.lakshan.user_service.repository.UserRepository;
import com.lakshan.user_service.repository.UserRoleRepository;
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

    public User addNewUser(User user) {
        if (user.getUserRole() != null && user.getUserRole().getId() != 0) {
            UserRole role = userRoleRepository.findById(user.getUserRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + user.getUserRole().getId()));
            user.setUserRole(role);
        }
        User saved = userRepository.save(user);
        return saved;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found with id: " + id)
        );
    }

    public User updateUser(User user){
        if(!userRepository.existsById(user.getId()))
            throw new RuntimeException("User not found with id: " + user.getId());

        if (user.getUserRole() != null && user.getUserRole().getId() != 0) {
            UserRole role = userRoleRepository.findById(user.getUserRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + user.getUserRole().getId()));
            user.setUserRole(role);
        }
        return userRepository.save(user);
    }

    public void deleteUser(int id){
        if(userRepository.existsById(id))
            userRepository.deleteById(id);
        else
            throw new RuntimeException("User not found with id: " + id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("User not found with email: " + email)
        );

    }
}
