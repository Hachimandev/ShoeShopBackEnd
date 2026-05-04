package com.fit.shoeshopbackend.repository;


import com.fit.shoeshopbackend.model.HoaDon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {

    @Query("SELECT h FROM HoaDon h WHERE h.maHoaDon LIKE :prefix% ORDER BY h.maHoaDon DESC")
    List<HoaDon> findByMaHoaDonStartingWithOrderByMaHoaDonDesc(String prefix, Pageable pageable);

    @Query("SELECT h FROM HoaDon h JOIN FETCH h.khachHang ORDER BY h.ngayDat DESC")
    List<HoaDon> findRecentWithCustomer(Pageable pageable);
}


