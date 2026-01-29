package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinhLuan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String noiDung;

    private int diemDanhGia;

    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhachHang")
    @JsonIgnoreProperties({"hoaDons", "binhLuans", "taiKhoan"})
    private KhachHang khachHang;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSanPham")
    @JsonIgnore
    private SanPham sanPham;
}