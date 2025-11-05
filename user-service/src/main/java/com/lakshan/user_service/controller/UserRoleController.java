package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.UserRole;
import com.lakshan.user_service.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Autowired
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/addUserRole")
    public ResponseEntity<UserRole> addUserRole(@RequestBody UserRole userRole){
        userRoleService.addNewUserRole(userRole);
        return ResponseEntity.ok(userRole);
    }

    @GetMapping("/getUserRole/{id}")
    public UserRole getUserRoleById(@PathVariable int id){
        return userRoleService.getUserRoleById(id);
    }

    @GetMapping("/getAllUserRoles")
    public List<UserRole> getUserRoles(){
        return userRoleService.getAllUserRoles();
    }

    @PutMapping("/updateUserRole")
    public ResponseEntity<UserRole> updateUserRole(@RequestBody UserRole userRole){
        userRoleService.updateUserRole(userRole);
        return ResponseEntity.ok(userRole);
    }

    @DeleteMapping("/deleteUserRole/{id}")
    public void deleteUserRole(@PathVariable int id){
        userRoleService.deleteUserRole(id);
    }
}
