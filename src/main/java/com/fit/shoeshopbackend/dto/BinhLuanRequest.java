package com.fit.shoeshopbackend.dto;

import lombok.Data;

@Data
public class BinhLuanRequest {
    private String maSanPham;
    private String username;
    private String noiDung;
    private int diemDanhGia;
}
