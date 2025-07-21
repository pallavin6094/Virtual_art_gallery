package com.virtualartgallery.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchasedArtworkDto {
    private Long artworkId;
    private String title;
    private String imageUrl;
    private LocalDateTime purchaseDate;
}

