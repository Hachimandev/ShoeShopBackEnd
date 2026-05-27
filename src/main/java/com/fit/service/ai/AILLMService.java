package com.fit.service.ai;

import com.fit.dto.ai.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AILLMService {
    
    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Generate response from LLM
     */
    public String generateResponse(String userMessage, List<ChatMessageDTO> conversationHistory) {
        try {
            // If OpenAI API key is not configured, return a mock response
            if (openAiApiKey == null || openAiApiKey.isEmpty()) {
                return generateMockResponse(userMessage);
            }

            return callOpenAIAPI(userMessage, conversationHistory);

        } catch (Exception e) {
            log.error("Error generating AI response", e);
            return generateMockResponse(userMessage);
        }
    }

    /**
     * Call OpenAI API for response
     */
    private String callOpenAIAPI(String userMessage, List<ChatMessageDTO> conversationHistory) {
        try {
            String url = "https://api.openai.com/v1/chat/completions";

            // Build request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("temperature", 0.7);

            // Build messages array
            ArrayNode messages = objectMapper.createArrayNode();
            
            // Add system message for shoe shop context
            ObjectNode systemMessage = objectMapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Bạn là trợ lý mua sắm AI cho một cửa hàng giày. " +
                    "Luôn trả lời bằng tiếng Việt, thân thiện, ngắn gọn và dễ hiểu. " +
                    "Hỗ trợ khách tìm giày, tư vấn sản phẩm, so sánh lựa chọn và hỗ trợ đặt hàng. " +
                    "Nếu thiếu thông tin như size, màu, ngân sách hoặc số lượng, hãy hỏi lại tự nhiên.");
            messages.add(systemMessage);

            // Add conversation history if available
            if (conversationHistory != null) {
                for (ChatMessageDTO msg : conversationHistory) {
                    ObjectNode historyMessage = objectMapper.createObjectNode();
                    historyMessage.put("role", msg.getRole());
                    historyMessage.put("content", msg.getContent());
                    messages.add(historyMessage);
                }
            }

            // Add current user message
            ObjectNode userMsgNode = objectMapper.createObjectNode();
            userMsgNode.put("role", "user");
            userMsgNode.put("content", userMessage);
            messages.add(userMsgNode);

            requestBody.set("messages", messages);

            // Make API call
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openAiApiKey);

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            var response = restTemplate.postForObject(url, request, ObjectNode.class);
            
            if (response != null && response.has("choices")) {
                return response.get("choices").get(0).get("message").get("content").asText();
            }

            return generateMockResponse(userMessage);

        } catch (Exception e) {
            log.error("OpenAI API error", e);
            return generateMockResponse(userMessage);
        }
    }

    /**
     * Generate mock response when API is not available
     */
    private String generateMockResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("find") || lowerMessage.contains("search")
                || lowerMessage.contains("tìm") || lowerMessage.contains("kiếm")
                || lowerMessage.contains("gợi ý") || lowerMessage.contains("cho xem")) {
            return "Mình đã tìm một vài mẫu giày phù hợp cho bạn. Bạn muốn xem thêm chi tiết hay đặt mẫu nào không?";
        } else if (lowerMessage.contains("order") || lowerMessage.contains("buy")
                || lowerMessage.contains("đặt") || lowerMessage.contains("mua")
                || lowerMessage.contains("thanh toán")) {
            return "Lựa chọn hay đó. Mình sẽ hỗ trợ bạn tạo đơn hàng với sản phẩm phù hợp nhất.";
        } else if (lowerMessage.contains("hello") || lowerMessage.contains("hi")
                || lowerMessage.contains("xin chào") || lowerMessage.contains("chào")) {
            return "Xin chào! Mình là trợ lý mua sắm của cửa hàng giày. Bạn muốn tìm giày theo màu, size, thương hiệu hay ngân sách nào?";
        } else if (lowerMessage.contains("price") || lowerMessage.contains("cost")
                || lowerMessage.contains("giá") || lowerMessage.contains("ngân sách")
                || lowerMessage.contains("bao nhiêu")) {
            return "Mình có thể giúp bạn tìm giày theo ngân sách. Ví dụ: \"tìm giày dưới 500000\" hoặc \"gợi ý giày Nike giá tốt\".";
        } else if (lowerMessage.contains("thanks") || lowerMessage.contains("thank")
                || lowerMessage.contains("cảm ơn")) {
            return "Rất vui được hỗ trợ bạn. Nếu cần tìm thêm mẫu khác, cứ nhắn mình nhé.";
        } else {
            return "Mình hiểu bạn đang quan tâm đến: \"" + userMessage + "\". " +
                    "Bạn có thể cho mình biết thêm màu, size, thương hiệu hoặc khoảng giá mong muốn không?";
        }
    }
}
