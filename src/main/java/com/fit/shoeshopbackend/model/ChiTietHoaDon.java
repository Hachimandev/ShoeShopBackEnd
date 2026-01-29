package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietHoaDon {
    @Id
    private String maChiTietHoaDon;
    private int soLuong;
    private double tongTien;

    @ManyToOne
    @JoinColumn(name = "maHoaDon")
    @JsonBackReference
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "maChiTietSanPham")
    private ChiTietSanPham chiTietSanPham;
}
