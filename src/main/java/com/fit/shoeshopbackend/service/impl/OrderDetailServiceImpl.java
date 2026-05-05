package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.OrderDetail;
import com.fit.shoeshopbackend.repository.OrderDetailRepository;
import com.fit.shoeshopbackend.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Override
    public Optional<OrderDetail> getOrderDetailById(String orderDetailId) {
        return orderDetailRepository.findById(orderDetailId);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(String orderId) {
        return orderDetailRepository.findByOrder_OrderId(orderId);
    }

    @Override
    public OrderDetail addOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail updateOrderDetail(String orderDetailId, OrderDetail orderDetail) {
        // Assume ID is set in the object or handled by JPA
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public void deleteOrderDetail(String orderDetailId) {
        orderDetailRepository.deleteById(orderDetailId);
    }
}
