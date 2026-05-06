package com.fit.shoeshopbackend.service;



import com.fit.shoeshopbackend.dto.Cart;
import com.fit.shoeshopbackend.dto.OrderResponseDTO;
import com.fit.shoeshopbackend.dto.OrderRequest;
import com.fit.shoeshopbackend.model.Order;
import com.fit.shoeshopbackend.model.OrderStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;



public interface OrderService {
    List<Order> getAllOrder();
    Optional<Order> getOrderById(String id);
    Order addOrder(Order Order);
    Order updateOrder(String id, Order Order);
    void deleteOrder(String id);

    double calculateFinalPrice(Cart cart);

    Object getCartSummary(Cart cart);

    OrderResponseDTO createOrderFromCart(OrderRequest request);
    Order updateOrderStatus(String orderId, OrderStatus newStatus);
    String getCustomerIdByUsername(String username);
    Order handleCancellationRequest(String orderId, boolean approve);
    List<Order> getRecentOrders(int limit);

    byte[] exportToExcel() throws IOException;
}










