package com.virtualartgallery.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentResponseDto {
    private String transactionId;
    private BigDecimal amount;
    private Long artworkId;
}