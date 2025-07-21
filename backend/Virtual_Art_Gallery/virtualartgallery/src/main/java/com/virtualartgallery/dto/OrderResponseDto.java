package com.virtualartgallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String artworkTitle;
    private String buyerUsername;
    private BigDecimal price; // ✅ Use BigDecimal instead of double 
}
