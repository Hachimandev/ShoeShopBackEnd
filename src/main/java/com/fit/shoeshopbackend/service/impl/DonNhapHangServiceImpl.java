package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.DonNhapHang;
import com.fit.shoeshopbackend.repository.DonNhapHangRepository;
import com.fit.shoeshopbackend.service.DonNhapHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonNhapHangServiceImpl implements DonNhapHangService {

    @Autowired
    private DonNhapHangRepository repository;

    @Override
    public List<DonNhapHang> getAllDonNhapHang() {
        return repository.findAll();
    }

    @Override
    public Optional<DonNhapHang> getDonNhapHangById(String maDonNhap) {
        return repository.findById(maDonNhap);
    }

    @Override
    public DonNhapHang addDonNhapHang(DonNhapHang donNhapHang) {
        return repository.save(donNhapHang);
    }

    @Override
    public DonNhapHang updateDonNhapHang(String maDonNhap, DonNhapHang donNhapHang) {
        donNhapHang.setMaDonNhap(maDonNhap);
        return repository.save(donNhapHang);
    }

    @Override
    public void deleteDonNhapHang(String maDonNhap) {
        repository.deleteById(maDonNhap);
    }
}
