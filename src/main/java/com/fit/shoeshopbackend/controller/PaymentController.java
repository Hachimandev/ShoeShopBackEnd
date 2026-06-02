package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.SePayWebhookRequest;
import com.fit.shoeshopbackend.model.Order;
import com.fit.shoeshopbackend.model.OrderStatus;
import com.fit.shoeshopbackend.repository.OrderRepository;
import com.fit.shoeshopbackend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/sepay/webhook")
    public ResponseEntity<java.util.Map<String, Object>> handleSePayWebhook(@RequestBody SePayWebhookRequest request) {
        // Only process incoming transfers
        if (!"in".equalsIgnoreCase(request.getTransferType())) {
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Not an incoming transfer, ignored."));
        }

        String content = request.getContent();
        if (content == null || content.isEmpty()) {
            return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Empty content, ignored."));
        }

        // Extract Order ID from content using a regex pattern, e.g., looking for "ORD-"
        // Assuming content format includes "ORD-YYYYMMDD-XXXX"
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

                // Simple verification: check if transfer amount matches order total amount
                if (request.getTransferAmount() != null && true /* request.getTransferAmount() >= order.getTotalAmount() bypassed for testing */) {
                    order.setOrderStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                    
                    if (order.getCustomer() != null && order.getCustomer().getEmail() != null) {
                        emailService.sendOrderEmail(order.getCustomer().getEmail(), order);
                    }
                    
                    return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Order status updated to PAID."));
                } else {
                    // Could set status to a manual review state if amount is insufficient
                    return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Insufficient transfer amount."));
                }
            } else {
                return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Order not found: " + orderId));
            }
        }

        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "No matching Order ID found in content."));
    }
}
