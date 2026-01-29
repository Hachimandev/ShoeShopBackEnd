package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {
    @Id
    private String maHoaDon;
    private LocalDateTime ngayDat;
    private int diemSuDung;
    private Double thanhTien;

    @Enumerated(EnumType.STRING)
    private TrangThaiHoaDon trangThaiHoaDon;

    @Enumerated(EnumType.STRING)
    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maKhachHang")
//    @JsonIgnore
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "maKhuyenMai")
    @JsonIgnore
    private KhuyenMai khuyenMai;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ChiTietHoaDon> chiTietHoaDons = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    @JsonIgnore
    private NhanVien nhanVien;

    @OneToOne(mappedBy = "hoaDon", cascade = CascadeType.ALL)
    @JsonIgnore
    private DonHuyTraHang donHuyTraHang;

}
