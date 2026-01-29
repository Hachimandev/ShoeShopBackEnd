package com.fit.shoeshopbackend.model;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDonNhapHang {
    @Id
    private String maChiTietDonNhapHang;
    private int soLuong;
    private double giaNhap;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "maDonNhap")
    private DonNhapHang donNhapHang;
}