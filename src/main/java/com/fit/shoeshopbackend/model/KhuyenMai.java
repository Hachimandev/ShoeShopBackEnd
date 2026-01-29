package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhuyenMai {
    @Id
    private String maKhuyenMai;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String dieuKien;
    private double chietKhau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNhanVien")
    @JsonIgnore
    private NhanVien nhanVien;


    @OneToMany(mappedBy = "khuyenMai")
    @JsonIgnore
    private List<HoaDon> hoaDons;
}

