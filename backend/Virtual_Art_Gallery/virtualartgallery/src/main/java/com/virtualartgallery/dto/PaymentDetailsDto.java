package com.virtualartgallery.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentDetailsDto {
    private String transactionId;
    private String artworkTitle;
    private BigDecimal totalPrice; 
    private String buyerUsername;
    private Long artworkId;
}

