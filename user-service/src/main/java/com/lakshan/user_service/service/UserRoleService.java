package com.lakshan.user_service.service;

import com.lakshan.user_service.entity.UserRole;
import com.lakshan.user_service.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public void addNewUserRole(UserRole userRole) {
        userRoleRepository.save(userRole);
    }

    public UserRole getUserRoleById(int id){
        return userRoleRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User Role not found with id: " + id)
        );
    }

    public List<UserRole> getAllUserRoles(){
        return userRoleRepository.findAll();
    }

    public void updateUserRole(UserRole userRole){
        if(userRoleRepository.existsById(userRole.getId()))
            userRoleRepository.save(userRole);
        else
            throw new RuntimeException("User Role not found with id: " + userRole.getId());
    }

    public void deleteUserRole(int id){
        if(userRoleRepository.existsById(id))
            userRoleRepository.deleteById(id);
        else
            throw new RuntimeException("User Role not found with id: " + id);
    }
}
