package com.fit.shoeshopbackend.service;




import com.fit.shoeshopbackend.model.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NhanVienService {
    Page<NhanVien> search(String search, String phongBan, String trangThai, Pageable pageable);

    List<NhanVien> searchAll(String search, String phongBan, String trangThai);

    Optional<NhanVien> findById(String id);

    NhanVien save(NhanVien nhanVien);

    Optional<NhanVien> update(String id, NhanVien nhanVien);

    void deleteById(String id);
}
