package com.fit.shoeshopbackend.service.impl;


import com.fit.shoeshopbackend.model.NhanVien;
import com.fit.shoeshopbackend.repository.NhanVienRepository;
import com.fit.shoeshopbackend.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository repository;

    @Autowired
    public NhanVienServiceImpl(NhanVienRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<NhanVien> search(String search, String phongBan, String trangThai, Pageable pageable) {
        Page<NhanVien> page = repository.findAll(pageable);

        List<NhanVien> filtered = page.getContent().stream()
                .filter(nv -> matches(nv, search, phongBan, trangThai))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    @Override
    public List<NhanVien> searchAll(String search, String phongBan, String trangThai) {
        return repository.findAll().stream()
                .filter(nv -> matches(nv, search, phongBan, trangThai))
                .collect(Collectors.toList());
    }

    private boolean matches(NhanVien nv, String search, String phongBan, String trangThai) {
        // Search by name or maNhanVien
        boolean searchOk = true;
        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            searchOk = (nv.getHoTen() != null && nv.getHoTen().toLowerCase().contains(s))
                    || (nv.getMaNhanVien() != null && nv.getMaNhanVien().toLowerCase().contains(s));
        }

        // Filter by phongBan
        boolean phongBanOk = true;
        if (phongBan != null && !phongBan.trim().isEmpty() && !phongBan.equals("all")) {
            try {
                phongBanOk = nv.getPhongBan() != null && nv.getPhongBan().name().equalsIgnoreCase(phongBan.trim());
            } catch (Exception e) {
                phongBanOk = false;
            }
        }

        // Filter by trangThai
        boolean trangThaiOk = true;
        if (trangThai != null && !trangThai.trim().isEmpty() && !trangThai.equals("all")) {
            try {
                trangThaiOk = nv.getTrangThaiLamViec() != null && nv.getTrangThaiLamViec().name().equalsIgnoreCase(trangThai.trim());
            } catch (Exception e) {
                trangThaiOk = false;
            }
        }

        return searchOk && phongBanOk && trangThaiOk;
    }

    @Override
    public Optional<NhanVien> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public NhanVien save(NhanVien nhanVien) {
        return repository.save(nhanVien);
    }

    @Override
    public Optional<NhanVien> update(String id, NhanVien nhanVien) {
        return repository.findById(id).map(existing -> {
            existing.setHoTen(nhanVien.getHoTen());
            existing.setSdt(nhanVien.getSdt());
            existing.setCccd(nhanVien.getCccd());
            existing.setNgaySinh(nhanVien.getNgaySinh());
            existing.setEmail(nhanVien.getEmail());
            existing.setImg(nhanVien.getImg());
            existing.setChucVu(nhanVien.getChucVu());
            existing.setPhongBan(nhanVien.getPhongBan());
            existing.setGioiTinh(nhanVien.getGioiTinh());
            existing.setTrangThaiLamViec(nhanVien.getTrangThaiLamViec());
            return repository.save(existing);
        });
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}

