package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.SePayWebhookRequest;
import com.fit.shoeshopbackend.model.Order;
import com.fit.shoeshopbackend.model.OrderStatus;
import com.fit.shoeshopbackend.repository.OrderRepository;
import com.fit.shoeshopbackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    @org.springframework.beans.factory.annotation.Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Transactional
    @GetMapping("/fix-db")
    public ResponseEntity<String> fixDb() {
        try {
            entityManager.createNativeQuery("ALTER TABLE orders MODIFY COLUMN order_status ENUM('PENDING', 'PAID', 'SHIPPING', 'DELIVERED', 'CANCELLED', 'RETURNED', 'AWAITING_CANCELLATION')").executeUpdate();
            return ResponseEntity.ok("Database Enum FIXED!");
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    @Transactional
    @PostMapping("/sepay/webhook")
    public ResponseEntity<java.util.Map<String, Object>> handleSePayWebhook(@RequestBody SePayWebhookRequest request) {
        try {
            // Only process incoming transfers
            if (!"in".equalsIgnoreCase(request.getTransferType())) {
                return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Not an incoming transfer, ignored."));
            }

            String content = request.getContent();
            if (content == null || content.isEmpty()) {
                return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Empty content, ignored."));
            }

            // Extract Order ID from content using a regex pattern, e.g., looking for "ORD-"
            Pattern pattern = Pattern.compile("(ORD-\\d{8}-\\d+)");
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String orderId = matcher.group(1);
                Optional<Order> optionalOrder = orderRepository.findById(orderId);

                if (optionalOrder.isPresent()) {
                    Order order = optionalOrder.get();
                    // Check if the order is already paid to avoid double processing
                    if (order.getOrderStatus() == OrderStatus.PAID || order.getOrderStatus() == OrderStatus.SHIPPING) {
                        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Order already processed."));
                    }

                    order.setOrderStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                    
                    try {
                        if (order.getCustomer() != null && order.getCustomer().getEmail() != null) {
                            emailService.sendOrderEmail(order.getCustomer().getEmail(), order);
                        }
                    } catch (Exception emailEx) {
                        System.err.println("Gửi email thất bại: " + emailEx.getMessage());
                        // Email thất bại không được làm ảnh hưởng đến việc lưu đơn hàng!
                    }
                    
                    return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Order status updated to PAID."));
                } else {
                    return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Order not found: " + orderId));
                }
            }

            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "No matching Order ID found in content."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("success", false, "error", "Internal error: " + ex.getMessage()));
        }
    }
}
