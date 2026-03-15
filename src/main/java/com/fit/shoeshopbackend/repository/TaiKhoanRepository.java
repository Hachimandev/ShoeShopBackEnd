package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
    boolean existsByEmail(String email);
    Optional<TaiKhoan> findByEmail(String email);
}

