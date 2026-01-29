package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPham {
    @Id
    private String maSanPham;
    private String tenSanPham;
    private String nuocSanXuat;
    private String moTa;
    private String chatLieu;
    private String thuongHieu;
    private double thue;
    private double giaBan;
    private String hinhAnh;

    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;

    @ManyToOne
    @JoinColumn(name = "maNhaCungCap")
    private NhaCungCap nhaCungCap;

    @ManyToOne
    @JoinColumn(name = "maLoai")
    private LoaiSanPham loaiSanPham;


    @ManyToMany
    @JoinTable(
            name = "SanPham_NhaCungCap",
            joinColumns = @JoinColumn(name = "maSanPham"),
            inverseJoinColumns = @JoinColumn(name = "maNhaCungCap")
    )
    @JsonIgnore
    private List<NhaCungCap> nhaCungCaps;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ChiTietSanPham> chiTietSanPhams;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BinhLuan> binhLuans;
}