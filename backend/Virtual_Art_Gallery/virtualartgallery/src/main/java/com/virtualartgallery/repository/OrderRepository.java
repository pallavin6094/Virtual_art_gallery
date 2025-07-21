package com.virtualartgallery.repository;

import com.virtualartgallery.entity.Artist;
import com.virtualartgallery.entity.Artwork;
import com.virtualartgallery.entity.Buyer;
import com.virtualartgallery.entity.Order;
import com.virtualartgallery.entity.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByArtwork_Artist_Id(Long artistId);

    // ✅ Find orders by Buyer (Fix for your error)
    List<Order> findByBuyer(Buyer buyer);

    // ✅ Fetch orders where the artwork belongs to the artist
    List<Order> findByArtwork_Artist(Artist artist);

    // ✅ Alternative way (if `findByArtwork_Artist` doesn't work)
    List<Order> findByArtworkIn(List<Artwork> artworks);

    long countByOrderDateAfter(LocalDateTime orderDate);
    Long countByOrderStatus(OrderStatus status);

    Optional<Order> findByArtworkIdAndBuyerId(Long artworkId, Long buyerId);

}
