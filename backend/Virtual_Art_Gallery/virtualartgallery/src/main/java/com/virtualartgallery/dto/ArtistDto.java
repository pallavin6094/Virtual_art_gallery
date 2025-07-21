package com.virtualartgallery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistDto {
    private Long id;
    private String bio;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private String username;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be exactly 10 digits and start with 6-9")
    private String phoneNumber;
    private String specialization;
    private String experience;
    private String location;
    private String profileImage;
    private Double rating;

    public ArtistDto(Long id, String bio, String email, String username, String phoneNumber, String specialization,
            String experience, String location, String profileImage, Double rating) {
        this.id = id;
        this.bio = bio;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.specialization = specialization;
        this.experience = experience;
        this.location = location;
        this.profileImage = profileImage;
        this.rating = rating;
    }
}
