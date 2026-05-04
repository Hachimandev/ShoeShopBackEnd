package com.fit.shoeshopbackend.dto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private String maChiTiet;
    private Integer soLuong;
    private String tenSanPham;
    private Double giaBan;
    private Integer size;
    private String mau;
    private Integer soLuongTonKho;
    private String hinhAnh;
}

