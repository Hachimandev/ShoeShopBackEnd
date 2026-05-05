package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.DonNhapHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonNhapHangRepository extends JpaRepository<DonNhapHang, String> {
}
