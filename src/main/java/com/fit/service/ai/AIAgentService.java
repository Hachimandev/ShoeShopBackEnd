package com.fit.service.ai;

import com.fit.dto.ai.*;
import com.fit.shoeshopbackend.model.Product;
import com.fit.shoeshopbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIAgentService {
    
    private final ProductRepository productRepository;
    private final AILLMService aiLLMService;

    /**
     * Process chat message with AI agent
     */
    public AIChatResponseDTO processMessage(AIChatRequestDTO request) {
        try {
            String userMessage = request.getMessage();
            log.info("Processing AI chat message: {}", userMessage);

            // Analyze user intent
            MessageAnalysis analysis = analyzeUserIntent(userMessage);
            log.info("Message analysis - Search intent: {}, Order intent: {}, Keywords: {}",
                    analysis.isSearchIntent(), analysis.isOrderIntent(), analysis.getKeywords());

            // Get LLM response for the message
            String aiResponse = aiLLMService.generateResponse(userMessage, request.getConversationHistory());

            // Search for products if intent is to search/find
            SuggestedProduct[] suggestedProducts = null;
            if (analysis.isSearchIntent()) {
                suggestedProducts = searchProducts(userMessage, analysis);
            }

            // Check if user wants to place an order
            OrderCreatedInfoDTO orderCreated = null;
            String actionPerformed = null;
            if (analysis.isOrderIntent() && suggestedProducts != null && suggestedProducts.length > 0) {
                // In a real scenario, we would extract product ID and quantity from the message
                // For now, we'll auto-order the top suggested product
                orderCreated = autoPlaceOrder(suggestedProducts, analysis);
                actionPerformed = "AUTO_ORDER_CREATED";
            }

            return AIChatResponseDTO.builder()
                    .message(aiResponse)
                    .suggestedProducts(suggestedProducts)
                    .autoOrderCreated(orderCreated)
                    .actionPerformed(actionPerformed)
                    .build();

        } catch (Exception e) {
            log.error("Error processing AI chat message", e);
            return AIChatResponseDTO.builder()
                    .message("Sorry, I encountered an error. Please try again.")
                    .build();
        }
    }

    /**
     * Analyze user message intent
     */
    private MessageAnalysis analyzeUserIntent(String message) {
        String lowerMessage = message.toLowerCase();
        MessageAnalysis analysis = new MessageAnalysis();

        // Keywords for search intent
        String[] searchKeywords = {"find", "search", "look for", "show", "want", "need", "color", 
                                   "size", "price", "cheap", "expensive", "brand", "shoe", "shoes",
                                   "tìm", "kiếm", "tìm kiếm", "gợi ý", "cho xem", "muốn", "cần",
                                   "màu", "kích cỡ", "giá", "rẻ", "đắt", "thương hiệu", "giày"};
        String[] orderKeywords = {"order", "buy", "purchase", "place order", "checkout", "get", 
                                 "add to cart", "i want", "can i have", "please send",
                                 "đặt", "mua", "đặt hàng", "thanh toán", "thêm vào giỏ", "lấy đôi này"};

        // Check for search intent
        for (String keyword : searchKeywords) {
            if (lowerMessage.contains(keyword)) {
                analysis.setSearchIntent(true);
                analysis.getKeywords().add(keyword);
            }
        }

        // Check for order intent
        for (String keyword : orderKeywords) {
            if (lowerMessage.contains(keyword)) {
                analysis.setOrderIntent(true);
                analysis.getKeywords().add(keyword);
            }
        }

        // Extract price range if mentioned
        extractPriceRange(message, analysis);

        return analysis;
    }

    /**
     * Extract price range from message
     */
    private void extractPriceRange(String message, MessageAnalysis analysis) {
        // Simple regex to find prices like "50", "$50", "under 100"
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("under") || lowerMessage.contains("below") 
                || lowerMessage.contains("dưới") || lowerMessage.contains("nhỏ hơn")) {
            // Extract max price
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(under|below|dưới|nhỏ hơn)\\s+(\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(lowerMessage);
            if (matcher.find()) {
                try {
                    int maxPrice = Integer.parseInt(matcher.group(2));
                    analysis.setMaxPrice(maxPrice);
                } catch (NumberFormatException e) {
                    log.debug("Could not parse price", e);
                }
            }
        }
    }

    /**
     * Search products based on user query
     */
    public SuggestedProduct[] searchProducts(String query, MessageAnalysis analysis) {
        List<Product> allProducts = productRepository.findAll();
        
        // Score products based on relevance
        List<ProductScore> scoredProducts = allProducts.stream()
                .map(product -> {
                    double score = calculateRelevanceScore(product, query, analysis);
                    return new ProductScore(product, score);
                })
                .filter(ps -> ps.score > 0)
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .collect(Collectors.toList());

        // Convert to DTOs and return top 5
        return scoredProducts.stream()
                .limit(5)
                .map(ps -> SuggestedProduct.builder()
                        .id(ps.product.getProductId())
                        .name(ps.product.getProductName())
                        .price(ps.product.getPrice())
                        .image(ps.product.getImage())
                        .relevanceScore(ps.score)
                        .build())
                .toArray(SuggestedProduct[]::new);
    }

    /**
     * Calculate relevance score for a product
     */
    private double calculateRelevanceScore(Product product, String query, MessageAnalysis analysis) {
        double score = 0;
        String lowerQuery = query.toLowerCase();
        String productName = product.getProductName() != null ? product.getProductName().toLowerCase() : "";
        String productCategory = (product.getCategory() != null && product.getCategory().getCategoryName() != null) ? 
                product.getCategory().getCategoryName().toLowerCase() : "";

        // Name matching (highest priority)
        if (productName.contains(lowerQuery)) {
            score += 1.0;
        } else {
            // Partial matching
            String[] keywords = lowerQuery.split("\\s+");
            for (String keyword : keywords) {
                if (!keyword.isEmpty() && (productName.contains(keyword) || productCategory.contains(keyword))) {
                    score += 0.3;
                }
            }
        }

        // Price filtering
        if (analysis != null && analysis.getMaxPrice() > 0 && product.getPrice() <= analysis.getMaxPrice()) {
            score += 0.2;
        }

        // Category matching
        if (productCategory.contains(lowerQuery)) {
            score += 0.5;
        }

        return score;
    }

    /**
     * Auto place order with suggested products
     */
    private OrderCreatedInfoDTO autoPlaceOrder(SuggestedProduct[] suggestedProducts, MessageAnalysis analysis) {
        try {
            if (suggestedProducts.length == 0) {
                return null;
            }

            // Get the top product and create order
            SuggestedProduct topProduct = suggestedProducts[0];
            int quantity = extractQuantityFromAnalysis(analysis);
            
            if (quantity == 0) {
                quantity = 1; // Default quantity
            }

            double totalAmount = topProduct.getPrice() * quantity;

            OrderItemInfoDTO item = OrderItemInfoDTO.builder()
                    .productId(topProduct.getId())
                    .productName(topProduct.getName())
                    .quantity(quantity)
                    .price(topProduct.getPrice())
                    .build();

            return OrderCreatedInfoDTO.builder()
                    .orderId(generateOrderId())
                    .status("PENDING")
                    .totalAmount(totalAmount)
                    .items(new OrderItemInfoDTO[]{item})
                    .build();

        } catch (Exception e) {
            log.error("Error creating auto order", e);
            return null;
        }
    }

    /**
     * Extract quantity from message analysis
     */
    private int extractQuantityFromAnalysis(MessageAnalysis analysis) {
        // Simple implementation - in real scenario, parse quantity from message
        return 1;
    }

    /**
     * Generate unique order ID
     */
    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis();
    }

    /**
     * Inner class for message analysis
     */
    private static class MessageAnalysis {
        private boolean searchIntent = false;
        private boolean orderIntent = false;
        private int maxPrice = 0;
        private Set<String> keywords = new HashSet<>();

        public boolean isSearchIntent() {
            return searchIntent;
        }

        public void setSearchIntent(boolean searchIntent) {
            this.searchIntent = searchIntent;
        }

        public boolean isOrderIntent() {
            return orderIntent;
        }

        public void setOrderIntent(boolean orderIntent) {
            this.orderIntent = orderIntent;
        }

        public int getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(int maxPrice) {
            this.maxPrice = maxPrice;
        }

        public Set<String> getKeywords() {
            return keywords;
        }
    }

    /**
     * Inner class for product scoring
     */
    private static class ProductScore {
        Product product;
        double score;

        ProductScore(Product product, double score) {
            this.product = product;
            this.score = score;
        }
    }
}
