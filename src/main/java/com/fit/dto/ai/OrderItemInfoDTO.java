package com.fit.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemInfoDTO {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
}
