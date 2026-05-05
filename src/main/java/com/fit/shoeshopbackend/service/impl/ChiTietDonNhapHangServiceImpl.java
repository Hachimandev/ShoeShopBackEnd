package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.ChiTietDonNhapHang;
import com.fit.shoeshopbackend.repository.ChiTietDonNhapHangRepository;
import com.fit.shoeshopbackend.service.ChiTietDonNhapHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChiTietDonNhapHangServiceImpl implements ChiTietDonNhapHangService {

    private final ChiTietDonNhapHangRepository repository;

    @Override
    public List<ChiTietDonNhapHang> getAllChiTietDonNhapHang() {
        return repository.findAll();
    }

    @Override
    public Optional<ChiTietDonNhapHang> getChiTietDonNhapHangById(String maChiTietDonNhapHang) {
        return repository.findById(maChiTietDonNhapHang);
    }

    @Override
    public List<ChiTietDonNhapHang> getChiTietByDonNhapHang(String maDonNhap) {
        return repository.findByDonNhapHangMaDonNhap(maDonNhap);
    }

    @Override
    public ChiTietDonNhapHang addChiTietDonNhapHang(ChiTietDonNhapHang chiTietDonNhapHang) {
        return repository.save(chiTietDonNhapHang);
    }

    @Override
    public ChiTietDonNhapHang updateChiTietDonNhapHang(String maChiTietDonNhapHang, ChiTietDonNhapHang chiTietDonNhapHang) {
        chiTietDonNhapHang.setMaChiTietDonNhapHang(maChiTietDonNhapHang);
        return repository.save(chiTietDonNhapHang);
    }

    @Override
    public void deleteChiTietDonNhapHang(String maChiTietDonNhapHang) {
        repository.deleteById(maChiTietDonNhapHang);
    }
}
