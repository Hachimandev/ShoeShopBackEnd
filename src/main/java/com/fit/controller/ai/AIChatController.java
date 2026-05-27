package com.fit.controller.ai;

import com.fit.dto.ai.*;
import com.fit.service.ai.AIAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequiredArgsConstructor
public class AIChatController {
    
    private final AIAgentService aiAgentService;

    /**
     * Process chat message
     */
    @PostMapping("/chat")
    public ResponseEntity<AIChatResponseDTO> chat(@RequestBody AIChatRequestDTO request) {
        log.info("Received chat message: {}", request.getMessage());
        try {
            AIChatResponseDTO response = aiAgentService.processMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing chat message", e);
            return ResponseEntity.internalServerError()
                    .body(AIChatResponseDTO.builder()
                            .message("An error occurred while processing your message.")
                            .build());
        }
    }

    /**
     * Search products via AI
     */
    @PostMapping("/search-products")
    public ResponseEntity<SuggestedProduct[]> searchProducts(@RequestBody ProductSearchRequestDTO request) {
        log.info("Searching products: {}", request.getQuery());
        try {
            // For now, we'll call the AI agent service's searchProducts method
            // In a real scenario, you might want to extract intent analysis
            SuggestedProduct[] products = aiAgentService.searchProducts(request.getQuery(), null);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error searching products", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create new chat session
     */
    @PostMapping("/chat-session")
    public ResponseEntity<Map<String, String>> createChatSession() {
        try {
            String sessionId = UUID.randomUUID().toString();
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", sessionId);
            log.info("Created new chat session: {}", sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating chat session", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get chat history
     */
    @GetMapping("/chat-history/{sessionId}")
    public ResponseEntity<ChatMessageDTO[]> getChatHistory(@PathVariable String sessionId) {
        log.info("Retrieving chat history for session: {}", sessionId);
        try {
            // In a real scenario, retrieve chat history from database
            return ResponseEntity.ok(new ChatMessageDTO[]{});
        } catch (Exception e) {
            log.error("Error retrieving chat history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check for AI service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "AI Chat Service");
        return ResponseEntity.ok(response);
    }
}
