package com.virtualartgallery.dto;

import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class BuyerDto {
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    private String username;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be exactly 10 digits and start with 6-9")
    private String contactNumber;
    private String address; // ✅ Add this field
    private List<OrderDto> orders;
    private List<CartItemResponseDto> cartItems;

    // ✅ Add a full constructor that matches all parameters
    public BuyerDto(Long id, String email, String username, String contactNumber, String address, List<OrderDto> orders, List<CartItemResponseDto> cartItems) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.contactNumber = contactNumber;
        this.address = address;
        this.orders = orders;
        this.cartItems = cartItems;
    }
}
