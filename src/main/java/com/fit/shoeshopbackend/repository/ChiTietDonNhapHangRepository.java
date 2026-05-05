package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.ChiTietDonNhapHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonNhapHangRepository extends JpaRepository<ChiTietDonNhapHang, String> {
    List<ChiTietDonNhapHang> findByDonNhapHangMaDonNhap(String maDonNhap);
}
