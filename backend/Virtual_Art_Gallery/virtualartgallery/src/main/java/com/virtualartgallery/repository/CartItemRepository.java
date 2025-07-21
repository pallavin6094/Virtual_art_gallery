package com.virtualartgallery.repository;

import com.virtualartgallery.entity.CartItem;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Get all cart items for a buyer
    List<CartItem> findByBuyer(Buyer buyer);

    // Find a specific artwork in the buyer's cart (helps prevent duplicates)
    Optional<CartItem> findByBuyerAndArtwork(Buyer buyer, Artwork artwork);

    // Remove all items from a buyer’s cart (useful for checkout or clearing cart)
    void deleteByBuyer(Buyer buyer);
}
