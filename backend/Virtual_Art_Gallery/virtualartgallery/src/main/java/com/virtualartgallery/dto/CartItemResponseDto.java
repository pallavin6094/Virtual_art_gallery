package com.virtualartgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponseDto {
    private Long cartItemId;
    private String artworkTitle;
    private String imageUrl;
}
