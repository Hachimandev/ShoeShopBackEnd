package com.fit.shoeshopbackend.service.impl;



import com.fit.shoeshopbackend.model.ChiTietSanPham;
import com.fit.shoeshopbackend.repository.ChiTietSanPhamRepository;
import com.fit.shoeshopbackend.service.ChiTietSanPhamService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChiTietSanPhamServiceImpl implements ChiTietSanPhamService {

    private final ChiTietSanPhamRepository repository;

    public ChiTietSanPhamServiceImpl(ChiTietSanPhamRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ChiTietSanPham> getAllChiTietSanPham() {
        return repository.findAll();
    }

    @Override
    public Optional<ChiTietSanPham> getChiTietSanPhamById(String id) {
        return repository.findById(id);
    }

    @Override
    public ChiTietSanPham addChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        return repository.save(chiTietSanPham);
    }

    @Override
    public ChiTietSanPham updateChiTietSanPham(String id, ChiTietSanPham chiTietSanPham) {
        chiTietSanPham.setMaChiTiet(id);
        return repository.save(chiTietSanPham);
    }

    @Override
    public void deleteChiTietSanPham(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<ChiTietSanPham> getChiTietSanPhamBySanPham(String maSanPham) {
        return repository.findBySanPham_MaSanPham(maSanPham);
    }
}

