package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.model.Order;

public interface EmailService {
    void sendOrderEmail(String to, Order Order);
}










