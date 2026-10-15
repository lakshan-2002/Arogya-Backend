package com.lakshan.user_service.controller;

import com.lakshan.user_service.entity.User;
import com.lakshan.user_service.exceptions.InvalidCredentialsException;
import com.lakshan.user_service.models.*;
import com.lakshan.user_service.security.JwtUtil;
import com.lakshan.user_service.service.PasswordResetService;
import com.lakshan.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;

    @Autowired
    public UserController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            PasswordResetService passwordResetService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest userRequest) {
        userService.addNewUser(userRequest);

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(userRequest.getUsername());
        userResponse.setEmail(userRequest.getEmail());
        userResponse.setUserRole(userRequest.getUserRole());

        return ResponseEntity.status(201).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequest userRequest) {
        var dbUser = userService.getUserByEmail(userRequest.getEmail());

        if(!passwordEncoder.matches(userRequest.getPassword(), dbUser.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(dbUser.getEmail(), dbUser.getUserRole().getRoleName());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(dbUser.getId());
        authResponse.setToken(token);
        authResponse.setUsername(dbUser.getUsername());
        authResponse.setEmail(dbUser.getEmail());
        authResponse.setRole(dbUser.getUserRole().getRoleName());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ApiResponse response = passwordResetService.sendOtp(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse response = passwordResetService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ApiResponse response = passwordResetService.resetPassword(
                request.getEmail(),
                request.getResetToken(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(userRequest.getUsername());
        userResponse.setEmail(userRequest.getEmail());
        userResponse.setUserRole(userRequest.getUserRole());

        userService.updateUser(userRequest);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/getUserByEmail/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }
}
