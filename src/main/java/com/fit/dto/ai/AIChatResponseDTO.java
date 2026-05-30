package com.fit.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIChatResponseDTO {
    private String message;
    private SuggestedProduct[] suggestedProducts;
    private OrderCreatedInfoDTO autoOrderCreated;
    private String actionPerformed;
    private String orderStep; // Track order flow: ASKING_FOR_ADDRESS, ASKING_FOR_CONFIRMATION, ORDER_CREATED
}
