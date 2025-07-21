package com.virtualartgallery.controller;

import com.virtualartgallery.dto.ArtworkDto;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;
import com.virtualartgallery.exception.ResourceNotFoundException;
import com.virtualartgallery.repository.ArtworkRepository;
import com.virtualartgallery.repository.BuyerRepository;
import com.virtualartgallery.repository.OrderRepository;
import com.virtualartgallery.service.ArtworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;
    //private final JwtUtil jwtUtil;
    private final ArtworkRepository artworkRepository;
    private final BuyerRepository buyerRepository;
    private final OrderRepository orderRepository;

    public ArtworkController(ArtworkService artworkService,ArtworkRepository artworkRepository,BuyerRepository buyerRepository,OrderRepository orderRepository) {
        this.artworkService = artworkService;
        this.artworkRepository = artworkRepository;
        this.buyerRepository = buyerRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<Artwork> uploadArtwork(@RequestBody Artwork artwork, Authentication authentication) {
        String artistUsername = authentication.getName();
        Artwork savedArtwork = artworkService.uploadArtwork(artwork, artistUsername);
        return ResponseEntity.ok(savedArtwork);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<ArtworkDto> editArtwork(@PathVariable Long id, @RequestBody Artwork updatedArtwork, Authentication authentication) {
        String artistUsername = authentication.getName();
        Artwork editedArtwork = artworkService.editArtwork(id, updatedArtwork, artistUsername);
        return ResponseEntity.ok(new ArtworkDto(editedArtwork));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArtworkById(@PathVariable Long id, Authentication authentication) {
        String artistUsername = authentication.getName();
        artworkService.deleteArtworkById(id, artistUsername);
        return ResponseEntity.ok("Artwork deleted successfully");
    }

    @DeleteMapping("/title/{title}")
    public ResponseEntity<String> deleteArtworkByTitle(@PathVariable String title, Authentication authentication) {
        String artistUsername = authentication.getName();
        artworkService.deleteArtworkByTitle(title, artistUsername);
        return ResponseEntity.ok("Artwork deleted successfully");
    }

    @GetMapping("/my-artworks")
    public ResponseEntity<List<Artwork>> getMyArtworks(Authentication authentication) {
        String artistUsername = authentication.getName();
        List<Artwork> artworks = artworkService.getArtistArtworks(artistUsername);
        return ResponseEntity.ok(artworks);
    }
    @GetMapping
    // public ResponseEntity<List<Artwork>> getAllArtworks() {
    //     List<Artwork> artworks = artworkService.getAllArtworks();
    //     return ResponseEntity.ok(artworks);
    public ResponseEntity<List<ArtworkDto>> getAllArtworks() {
        return ResponseEntity.ok(artworkService.getAllArtworks());
    }

    
    @GetMapping("/sales-report")
    public ResponseEntity<BigDecimal> getSalesReport(Authentication authentication) {
       String artistUsername = authentication.getName();
       BigDecimal earnings = artworkService.getSalesReport(artistUsername); // Ensure this returns BigDecimal
       return ResponseEntity.ok(earnings);
    }
   
    @GetMapping("/download/{artworkId}")
    public ResponseEntity<?> downloadArtwork(@PathVariable Long artworkId, Authentication authentication) {
    // ✅ Step 1: Extract authenticated user ID (assuming username is unique OR stored in JWT subject)
    String username = authentication.getName();

    // ✅ Step 2: Fetch Buyer using username (you can refactor to use user ID if available)
    Buyer buyer = buyerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

    Long buyerId = buyer.getId(); // <-- Use this ID for future operations

    // ✅ Step 3: Fetch artwork by ID
    Artwork artwork = artworkRepository.findById(artworkId)
            .orElseThrow(() -> new ResourceNotFoundException("Artwork not found"));

    // ✅ Step 4: Fetch Order using artwork + buyerId (ensure method exists)
    Order order = orderRepository.findByArtworkIdAndBuyerId(artworkId, buyerId)
            .orElseThrow(() -> new ResourceNotFoundException("You have not purchased this artwork"));

    // ✅ Step 5: Check payment status
    if (order.getOrderStatus() != OrderStatus.COMPLETED) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Payment not completed for this artwork");
    }

    // ✅ Step 6: Return Download URL
    return ResponseEntity.ok(Collections.singletonMap("downloadUrl", artwork.getImageUrl()));
}

}
