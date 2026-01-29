package com.fit.shoeshopbackend.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDonHuyTraHang {
    @Id
    private String maChiTietDonHuyTraHang;
    private int soLuong;
    private double tongTien;

    @ManyToOne
    @JoinColumn(name = "maDonHuyTraHang")
    private DonHuyTraHang donHuyTraHang;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;


    @ManyToOne
    @JoinColumn(name = "maChiTietSanPham")
    private ChiTietSanPham chiTietSanPham;
}

