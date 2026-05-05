package com.fit.shoeshopbackend.service;



import com.fit.shoeshopbackend.model.ReturnOrder;
import com.fit.shoeshopbackend.model.Order;

import java.util.List;
import java.util.Optional;

public interface ReturnOrderService {
    List<ReturnOrder> getAllReturnOrder();

    Optional<ReturnOrder> getReturnOrderById(String id);

    ReturnOrder addReturnOrder(ReturnOrder ReturnOrder);

    ReturnOrder updateReturnOrder(String id, ReturnOrder ReturnOrder);

    void deleteReturnOrder(String id);

    Order cancelOrder(String orderId, String customerId);

}









