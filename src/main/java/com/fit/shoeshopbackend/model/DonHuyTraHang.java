package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHuyTraHang {
    @Id
    private String maDonHuyTraHang;
    private LocalDateTime ngayHuyTraHang;
    private double tienHoan;

    @ManyToOne
    @JoinColumn(name = "maKhachHang")
    @JsonIgnore
    private KhachHang khachHang;

    @OneToOne
    @JoinColumn(name = "maHoaDon")
    @JsonIgnore
    private HoaDon hoaDon;

    @OneToMany(mappedBy = "donHuyTraHang", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChiTietDonHuyTraHang> chiTietDonHuyTraHangList;
}
