package com.virtualartgallery.service;

import com.virtualartgallery.entity.CartItem;
import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.ArtworkStatus;
import com.virtualartgallery.repository.CartItemRepository;
import com.virtualartgallery.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    // ✅ Get all cart items for a buyer
    public List<CartItem> getCartItems(Buyer buyer) {
        return cartItemRepository.findByBuyer(buyer);
    }

    // ✅ Add item to cart (with duplicate check)
    @Transactional
    public CartItem addToCart(Buyer buyer, Long artworkId) {
    Artwork artwork = artworkRepository.findById(artworkId)
            .orElseThrow(() -> new RuntimeException("Artwork not found"));

    // Check if the artwork is already SOLD
    if (artwork.getStatus() == ArtworkStatus.SOLD) { 
        throw new RuntimeException("This artwork has already been sold.");
    }

    // Check if the artwork is already in the buyer's cart
    Optional<CartItem> existingCartItem = cartItemRepository.findByBuyerAndArtwork(buyer, artwork);

    if (existingCartItem.isPresent()) {
        throw new RuntimeException("This artwork is already in your cart.");
    } else {
        // Add new item to the cart
        CartItem cartItem = new CartItem();
        cartItem.setBuyer(buyer);
        cartItem.setArtwork(artwork);
        return cartItemRepository.save(cartItem);
    }
}

    // ✅ Remove item from cart
    @Transactional
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // ✅ Clear the entire cart for a buyer
    @Transactional
    public void clearCart(Buyer buyer) {
        cartItemRepository.deleteByBuyer(buyer);
    }
}
