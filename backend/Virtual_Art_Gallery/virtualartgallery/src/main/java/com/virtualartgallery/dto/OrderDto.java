package com.virtualartgallery.dto;

import com.virtualartgallery.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderDto {
    private Long orderId;
    private String artworkTitle;
    private BigDecimal price;

    // Constructor to map Order entity to DTO
    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.artworkTitle = order.getArtwork().getTitle(); // Assuming Order has a relation with Artwork
        this.price = order.getTotalPrice();
    }

    public OrderDto(Long orderId, String artworkTitle, BigDecimal price) {
        this.orderId = orderId;
        this.artworkTitle = artworkTitle;
        this.price = price;
    }
}
