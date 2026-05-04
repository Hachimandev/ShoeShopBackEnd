package com.fit.shoeshopbackend.service;



import com.fit.shoeshopbackend.model.DonHuyTraHang;
import com.fit.shoeshopbackend.model.HoaDon;

import java.util.List;
import java.util.Optional;

public interface DonHuyTraHangService {
    List<DonHuyTraHang> getAllDonHuyTraHang();

    Optional<DonHuyTraHang> getDonHuyTraHangById(String id);

    DonHuyTraHang addDonHuyTraHang(DonHuyTraHang donHuyTraHang);

    DonHuyTraHang updateDonHuyTraHang(String id, DonHuyTraHang donHuyTraHang);

    void deleteDonHuyTraHang(String id);

    HoaDon cancelOrder(String maHoaDon, String maKhachHang);

}
