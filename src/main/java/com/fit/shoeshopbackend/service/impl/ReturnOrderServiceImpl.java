package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.*;
import com.fit.shoeshopbackend.repository.*;
import com.fit.shoeshopbackend.service.ReturnOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ReturnOrderServiceImpl implements ReturnOrderService {

    private final ReturnOrderRepository returnOrderRepository;
    private final OrderRepository orderRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CustomerRepository customerRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReturnOrderDetailRepository returnOrderDetailRepository;

    @Override
    public List<ReturnOrder> getAllReturnOrder() {
        return returnOrderRepository.findAll();
    }

    @Override
    public Optional<ReturnOrder> getReturnOrderById(String id) {
        return returnOrderRepository.findById(id);
    }

    @Override
    public ReturnOrder addReturnOrder(ReturnOrder returnOrder) {
        return returnOrderRepository.save(returnOrder);
    }

    @Override
    public ReturnOrder updateReturnOrder(String id, ReturnOrder returnOrder){
        returnOrder.setReturnOrderId(id);
        return returnOrderRepository.save(returnOrder);
    }

    @Override
    public void deleteReturnOrder(String id) {
        returnOrderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Order cancelOrder(String orderId, String customerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.PENDING) {
            if (!order.getCustomer().getCustomerId().equals(customerId)) {
                throw new SecurityException("You do not have permission to cancel this order.");
            }
        }
        else if (currentStatus == OrderStatus.SHIPPING) {
            if (!order.getCustomer().getCustomerId().equals(customerId)) {
                throw new SecurityException("You do not have permission to request cancellation for this order.");
            }
            order.setOrderStatus(OrderStatus.AWAITING_CANCELLATION);
            return orderRepository.save(order);
        } else if (currentStatus != OrderStatus.AWAITING_CANCELLATION) {
            throw new RuntimeException("Cannot cancel order in current status: " + currentStatus);
        }

        // Create ReturnOrder
        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setReturnOrderId(generateReturnOrderId());
        returnOrder.setReturnDate(LocalDateTime.now());
        returnOrder.setCustomer(order.getCustomer());
        returnOrder.setOrder(order);

        double refundAmount = 0.0;
        if (order.getPaymentMethod() != PaymentMethod.COD) {
            refundAmount = order.getTotalAmount();
        }
        returnOrder.setRefundAmount(refundAmount);

        // Process details and return stock
        List<ReturnOrderDetail> returnDetails = new ArrayList<>();
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderId(orderId);

        for (OrderDetail orderDetail : orderDetails) {
            ProductDetail productDetail = orderDetail.getProductDetail();
            if (productDetail == null) {
                throw new RuntimeException("Order detail has no linked product detail.");
            }
            
            // Return stock
            productDetail.setStockQuantity(productDetail.getStockQuantity() + orderDetail.getQuantity());
            productDetailRepository.save(productDetail);

            // Create ReturnDetail
            ReturnOrderDetail returnDetail = new ReturnOrderDetail();
            returnDetail.setReturnOrderDetailId(generateReturnOrderDetailId());
            returnDetail.setQuantity(orderDetail.getQuantity());
            returnDetail.setTotalPrice(orderDetail.getTotalPrice());
            returnDetail.setProductDetail(productDetail);
            returnDetail.setReturnOrder(returnOrder);
            returnDetails.add(returnDetail);
        }

        returnOrderRepository.save(returnOrder);
        returnOrderDetailRepository.saveAll(returnDetails);

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    private String generateReturnOrderId() {
        return "RTN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
    }

    private String generateReturnOrderDetailId() {
        return "RTND" + System.currentTimeMillis();
    }
}
