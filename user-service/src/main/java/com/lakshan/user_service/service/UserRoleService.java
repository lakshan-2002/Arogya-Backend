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

    public UserRole getUserRoleById(int id){
        return userRoleRepository.findById(id).orElseThrow(() ->
                new RuntimeException("User Role not found with id: " + id)
        );
    }

    public List<UserRole> getAllUserRoles(){
        return userRoleRepository.findAll();
    }

    public UserRole getUserRoleByName(String roleName){
        return userRoleRepository.findByRoleName(roleName).orElseThrow(() ->
                new RuntimeException("User Role not found with name: " + roleName)
        );
    }

}
