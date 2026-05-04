package com.fit.shoeshopbackend.dto;

import lombok.Data;

@Data
public class UpdateAccountRequest {
    private String email;
    private String hoTen;
    private String sdt;
    private String diaChi;
}