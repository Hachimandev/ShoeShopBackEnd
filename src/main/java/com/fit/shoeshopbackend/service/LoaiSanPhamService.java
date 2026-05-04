package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.model.LoaiSanPham;

import java.util.List;
import java.util.Optional;

public interface LoaiSanPhamService {
    List<LoaiSanPham> getAllLoaiSanPham();

    Optional<LoaiSanPham> getLoaiSanPhamById(String id);

    LoaiSanPham addLoaiSanPham(LoaiSanPham loaiSanPham);

    LoaiSanPham updateLoaiSanPham(String id, LoaiSanPham loaiSanPham);

    void deleteLoaiSanPham(String id);
}
