package com.virtualartgallery.dto;

import java.time.LocalDateTime;

import com.virtualartgallery.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8,max = 20,message = "Password must be between 8 and 20 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,20}$",
        message = "Password must include upper/lowercase letters ,numbers,and special characters"
    )
    private String password;
    private Role role;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;

    public UserDto(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public UserDto(Long id, String username, Role role, LocalDateTime registrationDate, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
    }    

}
