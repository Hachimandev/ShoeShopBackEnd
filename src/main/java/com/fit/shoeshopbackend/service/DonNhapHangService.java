package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.DonNhapHang;
import java.util.List;
import java.util.Optional;

public interface DonNhapHangService {
    List<DonNhapHang> getAllDonNhapHang();
    Optional<DonNhapHang> getDonNhapHangById(String maDonNhap);
    DonNhapHang addDonNhapHang(DonNhapHang donNhapHang);
    DonNhapHang updateDonNhapHang(String maDonNhap, DonNhapHang donNhapHang);
    void deleteDonNhapHang(String maDonNhap);
}
