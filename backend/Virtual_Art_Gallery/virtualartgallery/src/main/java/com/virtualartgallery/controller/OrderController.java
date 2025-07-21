package com.virtualartgallery.controller;

import com.virtualartgallery.dto.OrderRequestDto;
import com.virtualartgallery.dto.OrderResponseDto;
import com.virtualartgallery.dto.PurchasedArtworkDto;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.ArtworkStatus;
import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.exception.ResourceNotFoundException;
import com.virtualartgallery.repository.ArtworkRepository;
import com.virtualartgallery.repository.BuyerRepository;
import com.virtualartgallery.security.JwtUtil;
import com.virtualartgallery.service.OrderService;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;
    private final ArtworkRepository artworkRepository;
    private JwtUtil jwtUtil;
    private BuyerRepository buyerRepository;

    public OrderController(OrderService orderService,ArtworkRepository artworkRepository,JwtUtil jwtUtil,BuyerRepository buyerRepository) {
        this.orderService = orderService;
        this.artworkRepository = artworkRepository;
        this.jwtUtil = jwtUtil;
        this.buyerRepository=buyerRepository;
    }

    // ✅ Place Order (Secure)
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto orderRequest, Authentication authentication) {
        String buyerUsername = authentication.getName(); // Get the logged-in user's username
        Order order = orderService.placeOrder(orderRequest.getArtworkId(), buyerUsername);
        Long artworkId = orderRequest.getArtworkId(); 

        Artwork artwork = artworkRepository.findById(artworkId)
        .orElseThrow(() -> new ResourceNotFoundException("Artwork not found"));

        // 🔒 Check if artwork is already sold
        if (artwork.getStatus() == ArtworkStatus.SOLD) {
        throw new IllegalStateException("This artwork is already sold.");
    }

        OrderResponseDto responseDto = new OrderResponseDto(
                order.getId(),
                order.getArtwork().getTitle(),
                order.getBuyer().getUser().getUsername(),
                order.getTotalPrice()
                
        );
        System.out.println("Order Response DTO: " + responseDto);


        return ResponseEntity.ok(responseDto);
    }    

    @PostMapping("/checkout/single/{cartItemId}")
    public ResponseEntity<OrderResponseDto> checkoutSingleItem(@PathVariable Long cartItemId, Authentication authentication) {
    String buyerUsername = authentication.getName(); // Get logged-in user's username
    Order order = orderService.checkoutSingleItem(cartItemId, buyerUsername); // Call service method

    OrderResponseDto responseDto = new OrderResponseDto(
            order.getId(),
            order.getArtwork().getTitle(),
            order.getBuyer().getUser().getUsername(),
            order.getTotalPrice()

    );

    return ResponseEntity.ok(responseDto);
}

    // ✅ Get orders by buyer (Secure)
    @GetMapping("/buyer")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBuyer(Authentication authentication) {
        String buyerUsername = authentication.getName(); // Get logged-in user's username
        List<OrderResponseDto> orders = orderService.getOrdersByBuyer(buyerUsername);
        return ResponseEntity.ok(orders);
    }

    // ✅ Get artist earnings (Secure)
    @GetMapping("/earnings")
    public ResponseEntity<BigDecimal> getArtistEarnings(Authentication authentication) {
        String artistUsername = authentication.getName(); // Get logged-in artist username
        BigDecimal earnings = orderService.getArtistEarnings(artistUsername);
        return ResponseEntity.ok(earnings);
    }
    // ✅ Get order by orderId (to fetch artworkId etc.)
   @GetMapping("/buyer/purchased-artworks")
@PreAuthorize("hasAuthority('BUYER')")
public ResponseEntity<?> getPurchasedArtworks(@RequestHeader("Authorization") String authHeader) {
    try {
        // ✅ Remove "Bearer " prefix and extract JWT token
        String token = authHeader.replace("Bearer ", "");

        // ✅ Extract username from token
        String username = jwtUtil.extractUsername(token);

        // ✅ Get Buyer based on username
        Buyer buyer = buyerRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // ✅ Fetch purchased artworks for this buyer
        List<PurchasedArtworkDto> artworks = orderService.getPurchasedArtworksForBuyer(buyer.getId());

        return ResponseEntity.ok(artworks);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You are not authorized to view this content.");
    }
}



}
