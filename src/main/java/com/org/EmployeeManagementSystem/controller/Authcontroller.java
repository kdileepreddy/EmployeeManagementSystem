package com.org.EmployeeManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import com.org.EmployeeManagementSystem.Util.JWTUtil;
import com.org.EmployeeManagementSystem.entity.UserEntity;
import com.org.EmployeeManagementSystem.repository.UserRepository;
import com.org.EmployeeManagementSystem.model.LoginRequest;
import com.org.EmployeeManagementSystem.model.LoginResponse;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@Tag(name = "Authentication", description = "Authentication API")
public class Authcontroller {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User successfully registered"),
        @ApiResponse(responseCode = "400", description = "Username already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest loginRequest) {
        if (userRepository.findByUsername(loginRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.findByEmail(loginRequest.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(loginRequest.getUsername());
        user.setEmail(loginRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(loginRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        UserEntity savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        UserEntity user = userRepository.findByUsername(loginRequest.getUsername());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            String token = jwtUtil.generateToken(user.getUsername());
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            String expirationTime = jwtUtil.extractExpirationDate(token).toString();
            LoginResponse response = new LoginResponse(token, user.getUsername(), expirationTime);
            return ResponseEntity.ok().body(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @Operation(summary = "Get user details", description = "Retrieves the details of the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details"),
        @ApiResponse(responseCode = "401", description = "Invalid token"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@Parameter(description = "JWT token", required = true) @RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            UserEntity user = userRepository.findByUsername(username);
            if (user != null) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
