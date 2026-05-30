package com.fit.service.ai;

import com.fit.dto.ai.*;
import com.fit.shoeshopbackend.model.*;
import com.fit.shoeshopbackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIAgentService {
    
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AILLMService aiLLMService;

    /**
     * Process chat message with AI agent
     */
    public AIChatResponseDTO processMessage(AIChatRequestDTO request) {
        try {
            String userMessage = request.getMessage();
            String customerId = request.getCustomerId();
            log.info("Processing AI chat message: {} (customerId: {})", userMessage, customerId);

            // Analyze user intent
            MessageAnalysis analysis = analyzeUserIntent(userMessage);
            log.info("Message analysis - Search intent: {}, Order intent: {}, Keywords: {}",
                    analysis.isSearchIntent(), analysis.isOrderIntent(), analysis.getKeywords());

            // 1. Retrieve current order state from history
            ChatMessageDTO lastOrderMsg = getLastOrderFlowMessage(request.getConversationHistory());
            String lastStep = (lastOrderMsg != null && lastOrderMsg.getMetadata() != null) 
                    ? lastOrderMsg.getMetadata().getOrderStep() : null;
            SuggestedProduct[] historicProducts = (lastOrderMsg != null && lastOrderMsg.getMetadata() != null)
                    ? lastOrderMsg.getMetadata().getSuggestedProducts() : null;

            boolean isContinuingOrderFlow = lastStep != null && historicProducts != null && historicProducts.length > 0;

            // Abort old order flow if this is explicitly a new search or new order query
            if (isContinuingOrderFlow && isSearchOrNewOrderIntent(userMessage, analysis)) {
                isContinuingOrderFlow = false;
                lastStep = null;
                historicProducts = null;
            }

            // Search for products if intent is to search/find
            SuggestedProduct[] suggestedProducts = null;
            if (analysis.isSearchIntent()) {
                suggestedProducts = searchProducts(userMessage, analysis);
            }

            // Get LLM response for the message
            String aiResponse = aiLLMService.generateResponse(userMessage, request.getConversationHistory());

            // Check if user wants to place an order
            OrderCreatedInfoDTO orderCreated = null;
            String actionPerformed = null;
            String orderStep = null;
            if (isContinuingOrderFlow || (analysis.isOrderIntent() && suggestedProducts != null && suggestedProducts.length > 0)) {
                SuggestedProduct[] activeProducts = (suggestedProducts != null && suggestedProducts.length > 0) 
                        ? suggestedProducts : historicProducts;

                // Process order with customer information
                OrderFlowResult result = autoPlaceOrder(activeProducts, analysis, customerId, userMessage, lastStep, request.getConversationHistory());
                orderCreated = result.orderCreatedInfo;
                actionPerformed = result.actionPerformed;
                orderStep = result.orderStep;
                aiResponse = result.message != null ? result.message : aiResponse; // Override message if needed

                // Ensure the suggested product is preserved in the response so it is stored in history
                if (suggestedProducts == null || suggestedProducts.length == 0) {
                    suggestedProducts = activeProducts;
                }
            }

            return AIChatResponseDTO.builder()
                    .message(aiResponse)
                    .suggestedProducts(suggestedProducts)
                    .autoOrderCreated(orderCreated)
                    .actionPerformed(actionPerformed)
                    .orderStep(orderStep)
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
     * Auto place order with customer info validation
     */
    private OrderFlowResult autoPlaceOrder(SuggestedProduct[] suggestedProducts, MessageAnalysis analysis, 
                                           String customerId, String userMessage, String lastStep, List<ChatMessageDTO> conversationHistory) {
        try {
            if (suggestedProducts == null || suggestedProducts.length == 0) {
                return new OrderFlowResult(null, null, "NO_PRODUCTS", null);
            }

            // Get customer from DB
            Customer customer = null;
            if (customerId != null && !customerId.isEmpty()) {
                customer = customerRepository.findById(customerId).orElse(null);
            }

            if (customer == null) {
                return new OrderFlowResult(
                    null,
                    "Vui lòng đăng nhập để đặt hàng.",
                    "CUSTOMER_NOT_FOUND",
                    null
                );
            }

            log.info("Processing order for customer: {}", customer.getCustomerId());

            // If they want to cancel, abort the flow immediately
            if (isCancellationMessage(userMessage)) {
                return new OrderFlowResult(
                    null,
                    "Đã hủy yêu cầu đặt hàng.",
                    null,
                    null
                );
            }

            // Check if customer has address and phone
            boolean hasAddress = customer.getAddress() != null && !customer.getAddress().isEmpty();
            boolean hasPhone = customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty();

            // Try to extract address and phone from current message
            boolean isReplyingToAddressPrompt = "ASKING_FOR_ADDRESS".equals(lastStep);
            AddressPhoneInfo extractedInfo = extractAddressPhoneFromMessage(userMessage, !hasAddress, !hasPhone, isReplyingToAddressPrompt);

            // Update customer with extracted info if provided
            String newAddress = extractedInfo.address != null ? extractedInfo.address : customer.getAddress();
            String newPhone = extractedInfo.phone != null ? extractedInfo.phone : customer.getPhoneNumber();

            if (newAddress != null && !newAddress.isEmpty() && newPhone != null && !newPhone.isEmpty()) {
                if (extractedInfo.address != null || extractedInfo.phone != null) {
                    customer.setAddress(newAddress);
                    customer.setPhoneNumber(newPhone);
                    customerRepository.save(customer);
                    log.info("Updated customer info in DB: address={}, phone={}", newAddress, newPhone);
                }
                hasAddress = true;
                hasPhone = true;
            } else {
                // If we don't have both yet, but we extracted one, let's save the extracted one!
                if (extractedInfo.address != null) {
                    customer.setAddress(extractedInfo.address);
                    customerRepository.save(customer);
                    hasAddress = true;
                }
                if (extractedInfo.phone != null) {
                    customer.setPhoneNumber(extractedInfo.phone);
                    customerRepository.save(customer);
                    hasPhone = true;
                }
            }

            // If missing address or phone, ask for them
            if (!hasAddress && !hasPhone) {
                return new OrderFlowResult(
                    null,
                    "Vui lòng nhập địa chỉ và số điện thoại",
                    "ASKING_FOR_ADDRESS",
                    suggestedProducts
                );
            } else if (!hasAddress) {
                return new OrderFlowResult(
                    null,
                    "Vui lòng nhập địa chỉ",
                    "ASKING_FOR_ADDRESS",
                    suggestedProducts
                );
            } else if (!hasPhone) {
                return new OrderFlowResult(
                    null,
                    "Vui lòng nhập số điện thoại",
                    "ASKING_FOR_ADDRESS",
                    suggestedProducts
                );
            }

            // Check if user is confirming or denying the order
            boolean isConfirming = isConfirmationMessage(userMessage);
            int quantity = extractQuantityFromHistory(userMessage, conversationHistory);

            // If we were in ASKING_FOR_CONFIRMATION, we check for yes/no response
            if ("ASKING_FOR_CONFIRMATION".equals(lastStep)) {
                if (isConfirming) {
                    OrderCreatedInfoDTO createdOrder = createOrder(suggestedProducts[0], customer, analysis, quantity);
                    return new OrderFlowResult(
                        createdOrder,
                        String.format("Đơn hàng của bạn đã được tạo thành công! Mã đơn: %s", createdOrder.getOrderId()),
                        "ORDER_CREATED",
                        null
                    );
                } else if (isCancellationMessage(userMessage)) {
                    return new OrderFlowResult(
                        null,
                        "Đã hủy yêu cầu đặt hàng.",
                        null,
                        null
                    );
                } else {
                    // Prompt again for confirmation
                    SuggestedProduct topProduct = suggestedProducts[0];
                    String confirmMessage = String.format(
                        "Mình chưa rõ ý bạn. Bạn có xác nhận đặt %d đôi %s (tổng tiền: %,d ₫) giao tới địa chỉ %s, SĐT %s không? (Gõ 'có' hoặc 'không')",
                        quantity,
                        topProduct.getName(),
                        (long) (topProduct.getPrice() * quantity),
                        customer.getAddress(),
                        customer.getPhoneNumber()
                    );
                    return new OrderFlowResult(
                        null,
                        confirmMessage,
                        "ASKING_FOR_CONFIRMATION",
                        suggestedProducts
                    );
                }
            }

            // If we have all info but haven't asked for confirmation yet
            SuggestedProduct topProduct = suggestedProducts[0];
            String confirmMessage = String.format(
                "Mình sẽ đặt %d đôi %s (tổng tiền: %,d ₫) giao tới địa chỉ %s, SĐT %s. Bạn xác nhận chứ?",
                quantity,
                topProduct.getName(),
                (long) (topProduct.getPrice() * quantity),
                customer.getAddress(),
                customer.getPhoneNumber()
            );
            return new OrderFlowResult(
                null,
                confirmMessage,
                "ASKING_FOR_CONFIRMATION",
                suggestedProducts
            );

        } catch (Exception e) {
            log.error("Error in auto place order", e);
            return new OrderFlowResult(
                null,
                "Có lỗi xảy ra. Vui lòng thử lại.",
                "ERROR",
                null
            );
        }
    }

    /**
     * Create actual order in database
     */
    private OrderCreatedInfoDTO createOrder(SuggestedProduct topProduct, Customer customer, MessageAnalysis analysis, int quantity) {
        try {
            if (quantity <= 0) {
                quantity = 1;
            }

            // Find the Product entity by ID
            Product product = productRepository.findById(topProduct.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + topProduct.getId()));

            // Find a ProductDetail for this product (with available stock)
            ProductDetail productDetail = productDetailRepository.findAll().stream()
                    .filter(pd -> pd.getProduct().getProductId().equals(product.getProductId()) 
                            && pd.getStockQuantity() > 0)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No available product detail found"));

            double totalAmount = topProduct.getPrice() * quantity;

            // Create Order entity
            Order order = new Order();
            order.setOrderId(generateOrderId());
            order.setOrderDate(LocalDateTime.now());
            order.setTotalAmount(totalAmount);
            order.setUsedPoints(0);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setPaymentMethod(PaymentMethod.COD);
            order.setCustomer(customer);

            // Create OrderDetail entity
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderDetailId(generateOrderDetailId());
            orderDetail.setQuantity(quantity);
            orderDetail.setTotalPrice(totalAmount);
            orderDetail.setProduct(product);
            orderDetail.setProductDetail(productDetail);
            orderDetail.setOrder(order);

            // Update stock quantity
            productDetail.setStockQuantity(productDetail.getStockQuantity() - quantity);
            productDetailRepository.save(productDetail);

            // Set order details
            order.setOrderDetails(List.of(orderDetail));

            // Save order to database
            Order savedOrder = orderRepository.save(order);
            orderDetailRepository.save(orderDetail);

            log.info("Order created successfully: {} for customer: {} product: {}", 
                    savedOrder.getOrderId(), customer.getCustomerId(), product.getProductName());

            // Create order item info DTO
            OrderItemInfoDTO item = OrderItemInfoDTO.builder()
                    .productId(topProduct.getId())
                    .productName(topProduct.getName())
                    .quantity(quantity)
                    .price(topProduct.getPrice())
                    .build();

            return OrderCreatedInfoDTO.builder()
                    .orderId(savedOrder.getOrderId())
                    .status(savedOrder.getOrderStatus().toString())
                    .totalAmount(totalAmount)
                    .items(new OrderItemInfoDTO[]{item})
                    .orderLink("/profile/orders/" + savedOrder.getOrderId())
                    .build();

        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Extract address and phone from message
     */
    private AddressPhoneInfo extractAddressPhoneFromMessage(String message, boolean needsAddress, boolean needsPhone, boolean isReplyingToAddressPrompt) {
        AddressPhoneInfo info = new AddressPhoneInfo();
        
        // 1. Extract phone number: look for 9-11 digits
        java.util.regex.Pattern phonePattern = java.util.regex.Pattern.compile(
            "(?:sdt|số\\s*điện\\s*thoại|phone|đt)?\\s*:?\\s*(0[3|5|7|8|9][0-9]{8}|[0-9]{9,11})",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher phoneMatcher = phonePattern.matcher(message);
        if (phoneMatcher.find()) {
            info.phone = phoneMatcher.group(1).trim();
        }

        // 2. Extract address
        // Option A: Explicit address prefix
        java.util.regex.Pattern addressPattern = java.util.regex.Pattern.compile(
            "(?:địa\\s*chỉ|address)\\s*:?\\s*([^,\\n]+)",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher addressMatcher = addressPattern.matcher(message);
        if (addressMatcher.find()) {
            String addr = addressMatcher.group(1).trim();
            // Clean up if it contains phone number or keywords
            addr = addr.replaceAll("(?i)(?:sdt|sđt|phone|số|điện|đt).*", "").replaceAll("[,:\\-\\s]+$", "").trim();
            if (!addr.isEmpty() && addr.length() > 3) {
                info.address = addr;
            }
        }
        
        // Option B: If we still don't have the address and we are in the middle of replying to the address prompt:
        if (info.address == null && isReplyingToAddressPrompt) {
            if (needsAddress) {
                // If they need to provide an address, and they provided a phone number,
                // the remaining text is the address.
                if (info.phone != null) {
                    String cleanMsg = message.replaceAll("(?i)(?:sdt|sđt|phone|số|điện|đt)?\\s*:?\\s*" + info.phone, "")
                                             .replaceAll("(?i)địa\\s*chỉ|address", "")
                                             .replaceAll("[,;\\-\\s\\n]+", " ").trim();
                    if (cleanMsg.length() > 5) {
                        info.address = cleanMsg;
                    }
                } else {
                    // If they did not provide a phone number, the ENTIRE message is likely the address!
                    // Let's filter out general confirmations/denials like "ok", "yes", "có", "không"
                    String cleanMsg = message.trim();
                    if (cleanMsg.length() > 5 && !isConfirmationMessage(cleanMsg) && !isCancellationMessage(cleanMsg)) {
                        info.address = cleanMsg;
                    }
                }
            }
        }

        return info;
    }

    /**
     * Check if message is a cancellation (no, cancel, abort, etc.)
     */
    private boolean isCancellationMessage(String message) {
        String lowerMsg = message.toLowerCase().trim();
        String[] cancelKeywords = {"no", "cancel", "deny", "abort", "don't",
                                    "không", "hủy", "không đồng ý", "hủy bỏ", "thôi"};
        
        for (String keyword : cancelKeywords) {
            if (lowerMsg.equals(keyword) || lowerMsg.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find last assistant message in history that has an order flow step
     */
    private ChatMessageDTO getLastOrderFlowMessage(List<ChatMessageDTO> history) {
        if (history == null || history.isEmpty()) {
            return null;
        }
        for (int i = history.size() - 1; i >= 0; i--) {
            ChatMessageDTO msg = history.get(i);
            if ("assistant".equalsIgnoreCase(msg.getRole()) && msg.getMetadata() != null) {
                String step = msg.getMetadata().getOrderStep();
                if (step != null && !step.isEmpty()) {
                    // If order was already created, this flow is finished
                    if ("ORDER_CREATED".equals(step)) {
                        return null;
                    }
                    return msg;
                }
            }
        }
        return null;
    }

    /**
     * Check if message is a confirmation (yes, ok, agree, etc.)
     */
    private boolean isConfirmationMessage(String message) {
        String lowerMsg = message.toLowerCase().trim();
        String[] confirmKeywords = {"yes", "ok", "okay", "agree", "confirm", "correct", "right",
                                    "có", "được", "đồng ý", "xác nhận", "chính xác", "đúng", "ừ", "vâng"};
        
        for (String keyword : confirmKeywords) {
            if (lowerMsg.equals(keyword) || lowerMsg.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract quantity from a message string
     */
    private int extractQuantityFromMessage(String message) {
        if (message == null || message.isEmpty()) {
            return 1;
        }
        String lowerMsg = message.toLowerCase();

        // 1. Match patterns like "số lượng: 2", "số lượng là 2", "quantity: 2", "sl: 2", "sl 2", "qty 2"
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile(
            "(?:số\\s*lượng|quantity|sl|qty)\\s*(?:là|:)?\\s*([0-9]+)",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher1 = pattern1.matcher(lowerMsg);
        if (matcher1.find()) {
            try {
                int qty = Integer.parseInt(matcher1.group(1));
                if (qty > 0) return qty;
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        // 2. Match patterns like "2 đôi", "2 chiếc", "2 cái", "mua 2", "lấy 2"
        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile(
            "(?:mua|lấy)?\\s*([0-9]+)\\s*(?:đôi|chiếc|cái|cặp)?",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher2 = pattern2.matcher(lowerMsg);
        if (matcher2.find()) {
            try {
                String numStr = matcher2.group(1);
                int qty = Integer.parseInt(numStr);
                
                String matchedText = matcher2.group(0);
                if (matchedText.contains("đôi") || matchedText.contains("chiếc") 
                        || matchedText.contains("cái") || matchedText.contains("cặp")
                        || matchedText.startsWith("mua") || matchedText.startsWith("lấy")) {
                    if (qty > 0 && qty < 100) {
                        return qty;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return 1;
    }

    /**
     * Extract quantity from history and current message
     */
    private int extractQuantityFromHistory(String currentMessage, List<ChatMessageDTO> history) {
        int qty = extractQuantityFromMessage(currentMessage);
        if (qty > 1) {
            return qty;
        }

        if (history != null) {
            for (int i = history.size() - 1; i >= 0; i--) {
                ChatMessageDTO msg = history.get(i);
                if ("user".equalsIgnoreCase(msg.getRole())) {
                    qty = extractQuantityFromMessage(msg.getContent());
                    if (qty > 1) {
                        return qty;
                    }
                }
            }
        }
        return 1;
    }

    /**
     * Generate unique order ID
     */
    private String generateOrderId() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD-" + datePart + "-" + System.currentTimeMillis() % 10000;
    }

    /**
     * Generate unique order detail ID
     */
    private String generateOrderDetailId() {
        return "OD-" + UUID.randomUUID().toString().substring(0, 8);
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

    /**
     * Inner class for order flow result
     */
    private static class OrderFlowResult {
        OrderCreatedInfoDTO orderCreatedInfo;
        String message;
        String orderStep;
        String actionPerformed;
        SuggestedProduct[] suggestedProducts;

        OrderFlowResult(OrderCreatedInfoDTO orderCreatedInfo, String message, String orderStep, SuggestedProduct[] suggestedProducts) {
            this.orderCreatedInfo = orderCreatedInfo;
            this.message = message;
            this.orderStep = orderStep;
            this.suggestedProducts = suggestedProducts;
            // Set actionPerformed based on orderStep
            if ("ORDER_CREATED".equals(orderStep)) {
                this.actionPerformed = "AUTO_ORDER_CREATED";
            }
        }
    }

    /**
     * Check if the user message indicates a new search or a new order query
     */
    private boolean isSearchOrNewOrderIntent(String message, MessageAnalysis analysis) {
        String lowerMsg = message.toLowerCase();
        // If it explicitly contains address/phone keywords, it is NOT a search/new order intent
        if (lowerMsg.contains("địa chỉ") || lowerMsg.contains("address") 
                || lowerMsg.contains("sđt") || lowerMsg.contains("sdt") 
                || lowerMsg.contains("số điện thoại") || lowerMsg.contains("phone")) {
            return false;
        }
        
        // If it explicitly contains delivery/shipping keywords, it is NOT a search/new order intent
        if (lowerMsg.contains("giao") || lowerMsg.contains("nhận") || lowerMsg.contains("ship")) {
            return false;
        }
        
        // If it's a confirmation/cancellation, it's not a new search/order
        if (isConfirmationMessage(message) || isCancellationMessage(message)) {
            return false;
        }
        
        // Otherwise, check if it matches search or order intent
        return analysis.isSearchIntent() || analysis.isOrderIntent();
    }

    /**
     * Inner class for extracted address and phone
     */
    private static class AddressPhoneInfo {
        String address;
        String phone;
    }
}
