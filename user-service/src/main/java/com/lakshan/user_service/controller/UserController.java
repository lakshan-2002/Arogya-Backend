package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.User;
import com.lakshan.user_service.security.JwtUtil;
import com.lakshan.user_service.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> errorResponse = new HashMap<>();
    private static final String ERROR_MESSAGE_KEY = "message";

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userService.addNewUser(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        ResponseEntity<?> validationResponse = validateUserInput(user);
        if (validationResponse != null) return validationResponse;

        var dbUser = userService.getUserByEmail(user.getEmail());
        if (dbUser != null) {
            boolean ok = passwordEncoder.matches(user.getPassword(), dbUser.getPassword());
            if (!ok) {
                // legacy SHA-256 fallback and migrate to BCrypt
                String sha256Hex = DigestUtils.sha256Hex(user.getPassword());
                if (sha256Hex.equalsIgnoreCase(dbUser.getPassword())) {
                    dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    dbUser = userService.updateUser(dbUser);
                    ok = true;
                }
            }
            if (ok) {
                String token = jwtUtil.generateToken(dbUser);
                Map<String, Object> body = new HashMap<>();
                body.put("token", token);
                body.put("user", dbUser);
                return ResponseEntity.ok(body);
            }
        }
        errorResponse.put(ERROR_MESSAGE_KEY, "Invalid email or password");
        return ResponseEntity.status(401).body(errorResponse);
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
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/getUser/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public User getUser(@PathVariable int id){
        return userService.getUserById(id);
    }

    @GetMapping("/getUserByEmail/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == principal.email")
    public User getUserByEmail(@PathVariable String email){
        return userService.getUserByEmail(email);
    }

    @PutMapping("/updateUser")
    @PreAuthorize("hasRole('ADMIN') or #user.id == principal.id")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User updated = userService.updateUser(user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable int id){
        userService.deleteUser(id);
    }
}
