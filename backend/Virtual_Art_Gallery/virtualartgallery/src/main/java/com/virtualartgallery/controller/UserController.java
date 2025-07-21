package com.virtualartgallery.controller;

import com.virtualartgallery.dto.LoginRequest;
import com.virtualartgallery.dto.UserDto;
import com.virtualartgallery.service.UserService;

import jakarta.validation.Valid;

import com.virtualartgallery.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto userDto) {
        userService.registerUser(userDto);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest loginRequest) {
        String token = userService.loginUser(loginRequest);
        Long userId = userService.getUserIdByUsername(loginRequest.getUsername());
        String role = userService.getUserRoleByUsername(loginRequest.getUsername());

        // Create response object with token and message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful!");
        response.put("token", token);
        response.put("userId", String.valueOf(userId));
        response.put("role", role);

        return ResponseEntity.ok(response);
    }
    

    

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username,
                                            @RequestHeader("Authorization") String token) {
        try {
            // Extract username and role from JWT token
            String loggedInUsername = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            String role = jwtUtil.extractUserRole(token.replace("Bearer ", ""));

            // Check if the user is authorized to view the profile
            if ("ADMIN".equals(role) || loggedInUsername.equals(username)) {
                UserDto userDto = userService.getUserProfile(username);
                return ResponseEntity.ok(Map.of("message", "User profile fetched successfully!", "user", userDto));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
        }
    }
}
