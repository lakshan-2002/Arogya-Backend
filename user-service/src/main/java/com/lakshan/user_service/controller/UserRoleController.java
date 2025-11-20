package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.UserRole;
import com.lakshan.user_service.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "http://localhost:5173")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Autowired
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping("/getUserRole/{id}")
    public UserRole getUserRoleById(@PathVariable int id) {
        return userRoleService.getUserRoleById(id);
    }

    @GetMapping("/getAllUserRoles")
    public List<UserRole> getUserRoles() {
        return userRoleService.getAllUserRoles();
    }

    @GetMapping("/getUserRoleByName/{roleName}")
    public UserRole getUserRoleByName(@PathVariable String roleName) {
        return userRoleService.getUserRoleByName(roleName);
    }


}
