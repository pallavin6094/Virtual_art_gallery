package com.virtualartgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private Long artworkId;

    // Getters & Setters
    public Long getArtworkId() { return artworkId; }
    public void setArtworkId(Long artworkId) { this.artworkId = artworkId; }
}
