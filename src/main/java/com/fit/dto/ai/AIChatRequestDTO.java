package com.fit.dto.ai;

import com.fit.dto.ai.ChatMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIChatRequestDTO {
    private String message;
    private List<ChatMessageDTO> conversationHistory;
    private String userId; // Session ID
    private String customerId; // Customer ID for order creation
}
