package com.fit.shoeshopbackend.repository;


import com.fit.shoeshopbackend.model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, String> {

    @Query("SELECT k FROM KhuyenMai k WHERE k.ngayBatDau <= :ngayHienTai AND k.ngayKetThuc >= :ngayHienTai")
    List<KhuyenMai> findKhuyenMaiHopLe(@Param("ngayHienTai") Date ngayHienTai);

    List<KhuyenMai> findByMaKhuyenMaiContainingOrDieuKienContaining(String maKhuyenMai, String dieuKien);
    @Query(value = "SELECT maKhuyenMai FROM KhuyenMai ORDER BY maKhuyenMai DESC LIMIT 1")
    String findMaxMaKhuyenMai();
}