package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.model.ChiTietDonNhapHang;
import com.fit.shoeshopbackend.service.ChiTietDonNhapHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chitietdonnhaphang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChiTietDonNhapHangController {

    private final ChiTietDonNhapHangService chiTietDonNhapHangService;

    @GetMapping
    public ResponseEntity<List<ChiTietDonNhapHang>> getAllChiTietDonNhapHang() {
        return ResponseEntity.ok(chiTietDonNhapHangService.getAllChiTietDonNhapHang());
    }

    @GetMapping("/{maChiTietDonNhapHang}")
    public ResponseEntity<ChiTietDonNhapHang> getChiTietDonNhapHangById(@PathVariable String maChiTietDonNhapHang) {
        return chiTietDonNhapHangService.getChiTietDonNhapHangById(maChiTietDonNhapHang)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/donnhaphang/{maDonNhap}")
    public ResponseEntity<List<ChiTietDonNhapHang>> getChiTietByDonNhapHang(@PathVariable String maDonNhap) {
        return ResponseEntity.ok(chiTietDonNhapHangService.getChiTietByDonNhapHang(maDonNhap));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChiTietDonNhapHang> createChiTietDonNhapHang(@RequestBody ChiTietDonNhapHang chiTietDonNhapHang) {
        return ResponseEntity.ok(chiTietDonNhapHangService.addChiTietDonNhapHang(chiTietDonNhapHang));
    }

    @PutMapping("/{maChiTietDonNhapHang}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChiTietDonNhapHang> updateChiTietDonNhapHang(@PathVariable String maChiTietDonNhapHang, @RequestBody ChiTietDonNhapHang chiTietDonNhapHang) {
        return ResponseEntity.ok(chiTietDonNhapHangService.updateChiTietDonNhapHang(maChiTietDonNhapHang, chiTietDonNhapHang));
    }

    @DeleteMapping("/{maChiTietDonNhapHang}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChiTietDonNhapHang(@PathVariable String maChiTietDonNhapHang) {
        chiTietDonNhapHangService.deleteChiTietDonNhapHang(maChiTietDonNhapHang);
        return ResponseEntity.noContent().build();
    }
}
