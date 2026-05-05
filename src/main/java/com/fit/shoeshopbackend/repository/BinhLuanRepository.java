package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.BinhLuan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Long> {

    @Query("SELECT b FROM BinhLuan b JOIN FETCH b.khachHang WHERE b.sanPham.maSanPham = :maSanPham ORDER BY b.ngayTao DESC")
    List<BinhLuan> findBySanPhamIdWithKhachHang(String maSanPham);

    List<BinhLuan> findBySanPham_MaSanPhamOrderByNgayTaoDesc(String maSanPham);
}
