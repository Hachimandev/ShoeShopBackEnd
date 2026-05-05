package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.OrderDetail;
import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    List<OrderDetail> getAllOrderDetails();
    Optional<OrderDetail> getOrderDetailById(String orderDetailId);
    List<OrderDetail> getOrderDetailsByOrderId(String orderId);
    OrderDetail addOrderDetail(OrderDetail orderDetail);
    OrderDetail updateOrderDetail(String orderDetailId, OrderDetail orderDetail);
    void deleteOrderDetail(String orderDetailId);
}
