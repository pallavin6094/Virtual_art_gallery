package com.virtualartgallery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    private Long orderId;
    private String transactionId;
    private Boolean isSuccess;
    private BigDecimal amount; 
    private LocalDateTime purchaseDate;
    private Long artworkId;
}
