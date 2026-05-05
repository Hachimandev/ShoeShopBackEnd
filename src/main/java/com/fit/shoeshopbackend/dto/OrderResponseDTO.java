package com.fit.shoeshopbackend.dto;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private String orderId;
    private double totalAmount;
    private String orderStatus;
}
