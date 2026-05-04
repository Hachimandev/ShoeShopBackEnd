package com.fit.shoeshopbackend.dto;



import lombok.Data;

@Data
public class OrderRequest {
    private UserInfo userInfo;
    private Cart cart;
    private double thanhTien;
}

