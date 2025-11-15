package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.User;
import com.lakshan.user_service.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final Map<String, String> errorResponse = new HashMap<>();
    private static final String ERROR_MESSAGE_KEY = "message";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User user){
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        userService.addNewUser(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        ResponseEntity<?> validationResponse = validateUserInput(user);

        if (validationResponse != null) {
            return validationResponse;
        }
        var dbUser = userService.getUserByEmail(user.getEmail());

        if (dbUser != null && dbUser.getPassword().equals(DigestUtils.sha256Hex(user.getPassword()))) {
            return ResponseEntity.ok(dbUser);
        } else {
            errorResponse.put(ERROR_MESSAGE_KEY, "Invalid email or password");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    private ResponseEntity<?> validateUserInput(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank()) {
            errorResponse.put(ERROR_MESSAGE_KEY, "Email and password must not be empty");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errorResponse.put(ERROR_MESSAGE_KEY, "Invalid email format");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return null;
    }
    @GetMapping("/getAllUsers")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUserById(id);
    }

    @GetMapping("/getUserByEmail/{email}")
    public User getUserByEmail(@PathVariable String email){
        return userService.getUserByEmail(email);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable int id){
        userService.deleteUser(id);
    }



}
