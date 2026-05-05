package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.OrderRequest;
import com.fit.shoeshopbackend.dto.OrderResponseDTO;
import com.fit.shoeshopbackend.model.Order;
import com.fit.shoeshopbackend.model.OrderStatus;
import com.fit.shoeshopbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrderFromCart(request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable String id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable String id, @RequestParam String customerId, @RequestParam boolean approve) {
        return ResponseEntity.ok(orderService.handleCancellationRequest(id, approve));
    }
}
