package com.virtualartgallery.controller;

import com.virtualartgallery.dto.BuyerDto;
import com.virtualartgallery.dto.CartItemResponseDto;
import com.virtualartgallery.dto.OrderDto;

import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.repository.BuyerRepository;
import com.virtualartgallery.service.BuyerService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/buyers")
public class BuyerController {

    @Autowired
    private BuyerService buyerService;
    
    @Autowired
    private BuyerRepository buyerRepository;

    @PostMapping("/create")
    public ResponseEntity<String> createArtist( @Valid @RequestBody BuyerDto buyerDto,
                                           @AuthenticationPrincipal UserDetails userDetails) {
    String loggedInEmail = userDetails.getUsername();  // Get the logged-in user's username
    buyerService.createBuyer(buyerDto, loggedInEmail);
    return ResponseEntity.ok("Buyer profile created successfully!");
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<BuyerDto> updateBuyer(@PathVariable Long userId,@Valid @RequestBody BuyerDto buyerDto) {
        BuyerDto updatedBuyer = buyerService.updateBuyer(userId, buyerDto);
        return ResponseEntity.ok(updatedBuyer);
    }

    // ✅ Get Buyer by Username
    @GetMapping("/{userId}")
    public ResponseEntity<BuyerDto> getBuyerByUserId(@PathVariable Long userId) {
        Buyer buyer = buyerRepository.findByUser_Id(userId)

                .orElseThrow(() -> new RuntimeException("Buyer not found with userID: " + userId));

        // Convert Order entity list to OrderDto list
        List<OrderDto> orderDtos = buyer.getOrders().stream()
                .map(order -> new OrderDto(order.getId(), order.getArtwork().getTitle(), order.getTotalPrice()))
                .collect(Collectors.toList());

        // Convert CartItems to CartItemResponseDto list
        List<CartItemResponseDto> cartItems = buyer.getCartItems().stream()
                .map(cartItem -> new CartItemResponseDto(
                    cartItem.getId(),
                    cartItem.getArtwork().getTitle(),
                    cartItem.getArtwork().getImageUrl()
                )).collect(Collectors.toList());

        // Create BuyerDto object after initializing orders and cartItems
        BuyerDto buyerDto = new BuyerDto(
                buyer.getId(),
                buyer.getEmail(),
                buyer.getUser().getUsername(),
                buyer.getContactNumber(),
                buyer.getAddress(),
                orderDtos,  // Pass the list of OrderDto
                cartItems   // Pass the list of CartItemResponseDto
        );

        return ResponseEntity.ok(buyerDto);
    }
   

}