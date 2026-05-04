package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.model.ChiTietSanPham;

import java.util.List;
import java.util.Optional;

public interface ChiTietSanPhamService {
    List<ChiTietSanPham> getAllChiTietSanPham();

    Optional<ChiTietSanPham> getChiTietSanPhamById(String id);

    ChiTietSanPham addChiTietSanPham(ChiTietSanPham chiTietSanPham);

    ChiTietSanPham updateChiTietSanPham(String id, ChiTietSanPham chiTietSanPham);

    void deleteChiTietSanPham(String id);
    List<ChiTietSanPham> getChiTietSanPhamBySanPham(String maSanPham);
}

