package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPham {
    @Id
    private String maChiTiet;
    private String mau;
    private int size;
    private int soLuongTonKho;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    @JsonBackReference
    private SanPham sanPham;
}