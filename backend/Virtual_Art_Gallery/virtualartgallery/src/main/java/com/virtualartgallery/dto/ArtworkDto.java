package com.virtualartgallery.dto;

import java.math.BigDecimal;

import com.virtualartgallery.entity.Artwork;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkDto {
    private Long id; 
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private String status;
    private Long artistId;  // Now referring to User ID
    private String artistName;
    private Double artistRating;
    private String artistSpecialization;

    public ArtworkDto(Artwork artwork) {
        this.id = artwork.getId();
        this.artistId = (artwork.getArtist() != null) ? artwork.getArtist().getId() : null; // Ensure artist is not null
        this.title = artwork.getTitle();
        this.description = artwork.getDescription();
        this.price = artwork.getPrice();
        this.category = artwork.getCategory();
        this.imageUrl = artwork.getImageUrl();
        this.status= artwork.getStatus().name();

        if (artwork.getArtist() != null && artwork.getArtist().getUser() != null) {
            this.artistName = artwork.getArtist().getUser().getUsername();
            this.artistRating = artwork.getArtist().getRating();
            this.artistSpecialization = artwork.getArtist().getSpecialization();
        }
    }
}
