package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.model.HoaDon;

public interface EmailService {
    void sendOrderEmail(String to, HoaDon hoaDon);
}

