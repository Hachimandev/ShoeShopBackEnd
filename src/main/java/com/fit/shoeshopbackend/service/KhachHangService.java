package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.model.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KhachHangService {
    int layDiemTichLuyTheoUsername(String username);
    KhachHang findByTaiKhoan_TenDangNhap(String username);
    KhachHang save(KhachHang kh);
    String getKhachHangIdByUsername(String username);
    List<KhachHang> getAllCustomers();
    Page<KhachHang> searchCustomers(String search, Double minSpend, Double maxSpend, String startDate, String endDate, Pageable pageable);
    long countNewCustomersThisMonth();
    double calculateTotalSpendingByCustomer(String maKhachHang);
}

