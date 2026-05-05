package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.ChiTietDonNhapHang;
import java.util.List;
import java.util.Optional;

public interface ChiTietDonNhapHangService {
    List<ChiTietDonNhapHang> getAllChiTietDonNhapHang();
    Optional<ChiTietDonNhapHang> getChiTietDonNhapHangById(String maChiTietDonNhapHang);
    List<ChiTietDonNhapHang> getChiTietByDonNhapHang(String maDonNhap);
    ChiTietDonNhapHang addChiTietDonNhapHang(ChiTietDonNhapHang chiTietDonNhapHang);
    ChiTietDonNhapHang updateChiTietDonNhapHang(String maChiTietDonNhapHang, ChiTietDonNhapHang chiTietDonNhapHang);
    void deleteChiTietDonNhapHang(String maChiTietDonNhapHang);
}
