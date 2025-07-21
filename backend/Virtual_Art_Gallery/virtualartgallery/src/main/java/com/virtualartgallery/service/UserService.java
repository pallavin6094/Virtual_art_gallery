package com.virtualartgallery.service;

import com.virtualartgallery.dto.LoginRequest;
import com.virtualartgallery.dto.UserDto;
import com.virtualartgallery.entity.Role;
import com.virtualartgallery.entity.User;
import com.virtualartgallery.repository.UserRepository;
import com.virtualartgallery.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void registerUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Default role if not provided
        user.setRole(userDto.getRole() != null ? userDto.getRole() : Role.BUYER);
        user.setRegistrationDate(LocalDateTime.now());
        
        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
    }

    public String loginUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            logger.warn("Login failed - User not found: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed - Invalid password for username: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        // ✅ Update last login time before generating token
        user.setLastLogin(LocalDateTime.now());  
        userRepository.save(user);  

        // String token = jwtUtil.generateToken(user.getUsername(), user.getRole().toString());
        String token = jwtUtil.generateToken( user.getUsername(), user.getRole().toString());

        logger.info("User logged in successfully: {}", loginRequest.getUsername());
        return token;
    }

    public UserDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Do not return password
        return new UserDto(user.getUsername(), user.getRole());
    }
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
    public String getUserRoleByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get().getRole().name(); // ✅ Convert enum to string
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
    
    
    

    
}
