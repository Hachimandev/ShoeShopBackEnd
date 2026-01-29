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
public class DonNhapHang {
    @Id
    private String maDonNhap;
    private Date ngayNhap;

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    @JsonIgnore
    private NhanVien nhanVien;

    @OneToMany(mappedBy = "donNhapHang", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChiTietDonNhapHang> chiTietDonNhapHangList;
}
