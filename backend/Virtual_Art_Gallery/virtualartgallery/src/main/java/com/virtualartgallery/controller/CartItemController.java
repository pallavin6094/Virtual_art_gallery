package com.virtualartgallery.controller;

import com.virtualartgallery.dto.CartItemRequestDto;
import com.virtualartgallery.dto.CartItemResponseDto;
import com.virtualartgallery.entity.CartItem;
import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.service.CartItemService;
import com.virtualartgallery.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@RequestMapping("/api/cart")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private BuyerService buyerService;

    private Buyer getAuthenticatedBuyer(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("User not authenticated");
        }
        String username = userDetails.getUsername();
        return buyerService.getBuyerByUsername(username);  // ✅ Now correctly returns Buyer
    }
    

    // ✅ Get cart items
    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponseDto>> getCartItems(@AuthenticationPrincipal UserDetails userDetails) {
        Buyer buyer = getAuthenticatedBuyer(userDetails);
        List<CartItem> cartItems = cartItemService.getCartItems(buyer);

        // Convert Entity -> DTO before sending response
        List<CartItemResponseDto> cartItemDtos = cartItems.stream()
                .map(item -> new CartItemResponseDto(item.getId(), 
                                                     item.getArtwork().getTitle(),
                                                     item.getArtwork().getImageUrl()))
                                                     
                .collect(Collectors.toList());

        return ResponseEntity.ok(cartItemDtos);
    }

    // ✅ Add an item to the cart
    @PostMapping("/add")
    public ResponseEntity<CartItemResponseDto> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestBody CartItemRequestDto requestDto) {
        Buyer buyer = getAuthenticatedBuyer(userDetails);
        CartItem cartItem = cartItemService.addToCart(buyer, requestDto.getArtworkId());

        // Convert to DTO before returning
        CartItemResponseDto responseDto = new CartItemResponseDto(cartItem.getId(), 
                                                                  cartItem.getArtwork().getTitle(),
                                                                  cartItem.getArtwork().getImageUrl());

        return ResponseEntity.status(201).body(responseDto);
    }

    // ✅ Remove an item from the cart
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long cartItemId) {
        cartItemService.removeFromCart(cartItemId);
        return ResponseEntity.ok("Item removed from cart successfully");
    }

    // ✅ Clear the entire cart for the buyer
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        Buyer buyer = getAuthenticatedBuyer(userDetails);
        cartItemService.clearCart(buyer);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
