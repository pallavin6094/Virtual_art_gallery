package com.virtualartgallery.service;

import com.virtualartgallery.dto.BuyerDto;
import com.virtualartgallery.dto.PurchasedArtworkDto;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.entity.PaymentStatus;
import com.virtualartgallery.entity.User;
import com.virtualartgallery.repository.BuyerRepository;
import com.virtualartgallery.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;

    public BuyerService(BuyerRepository buyerRepository,UserRepository userRepository) {
        this.buyerRepository = buyerRepository;
        this.userRepository = userRepository;
    }

    public void createBuyer(BuyerDto buyerDto, String username) {
        // Find the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if the artist profile already exists
        // if (buyerRepository.existsById(user.getId())) {
        //     throw new RuntimeException("Buyer profile already exists!");
        // }
        if (buyerRepository.existsByUser_Username(username)) {
    throw new RuntimeException("Buyer profile already exists!");
}


        // Create a new Artist entity
        Buyer buyer = new Buyer();
        buyer.setUser(user);
        buyer.setEmail(buyerDto.getEmail());
        buyer.setAddress(buyerDto.getAddress());
        buyer.setContactNumber(buyerDto.getContactNumber());

        // Save the artist profile
        buyerRepository.save(buyer);
    }

    public BuyerDto updateBuyer(Long userId, BuyerDto buyerDto) {
        Buyer buyer = buyerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Buyer not found with username: " + userId));

        // Update the buyer fields
        buyer.setEmail(buyerDto.getEmail());
        buyer.setContactNumber(buyerDto.getContactNumber());
        buyer.setAddress(buyerDto.getAddress());

        Buyer updatedBuyer = buyerRepository.save(buyer);

        // Convert updated Buyer entity to BuyerDto and return
        return new BuyerDto(
                updatedBuyer.getId(),
                updatedBuyer.getEmail(),
                updatedBuyer.getUser().getUsername(),
                updatedBuyer.getContactNumber(),
                updatedBuyer.getAddress(),
                null, // Orders (optional)
                null  // Cart Items (optional)
        );
    }

    public Buyer getBuyerByUserId(Long userId) {
    return buyerRepository.findByUser_Id(userId)
        .orElseThrow(() -> new RuntimeException("Buyer not found with username: " + userId));
}

public Buyer getBuyerByUsername(String username) {
    return buyerRepository.findByUser_Username(username)
        .orElseThrow(() -> new RuntimeException("Buyer not found with username: " + username));
}

}
