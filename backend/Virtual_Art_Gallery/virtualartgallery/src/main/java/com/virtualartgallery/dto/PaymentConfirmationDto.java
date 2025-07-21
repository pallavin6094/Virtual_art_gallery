package com.virtualartgallery.dto;


import lombok.Data;

@Data

public class PaymentConfirmationDto {
    private String transactionId;
    private String status;

}