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
public class NhanVien {
    @Id
    private String maNhanVien;
    private String hoTen;
    private String email;
    private String sdt;
    private String cccd;
    private String img;
    private Date ngaySinh;
    @Enumerated(EnumType.STRING)
    private TrangThaiLamViec trangThaiLamViec;

    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;

    @Enumerated(EnumType.STRING)
    private ChucVu chucVu;

    @Enumerated(EnumType.STRING)
    private PhongBan phongBan;

    @OneToOne
    @JoinColumn(name = "maTaiKhoan")
    @JsonIgnore
    private TaiKhoan taiKhoan;


    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<KhuyenMai> khuyenMais;


    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DonNhapHang> donNhapHangs;
}
