package com.fit.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedInfoDTO {
    private String orderId;
    private String status;
    private double totalAmount;
    private OrderItemInfoDTO[] items;
    private String orderLink; // Link to view order details
}
