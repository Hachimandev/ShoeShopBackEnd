package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, String> {
    List<ChiTietSanPham> findBySanPham_MaSanPham(String maSanPham);
}
