package com.fit.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageMetadataDTO {
    private SuggestedProduct[] suggestedProducts;
    private OrderCreatedInfoDTO orderCreated;
    private String actionTaken;
}
