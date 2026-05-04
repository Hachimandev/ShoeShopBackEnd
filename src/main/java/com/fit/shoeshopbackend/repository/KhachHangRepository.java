package com.fit.shoeshopbackend.repository;


import com.fit.shoeshopbackend.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, String> {
    KhachHang findByTaiKhoan_TenDangNhap(String tenDangNhap);
    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findByTaiKhoan_MaTaiKhoan(String maTaiKhoan);
}