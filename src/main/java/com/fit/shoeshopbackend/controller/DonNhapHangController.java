package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.model.DonNhapHang;
import com.fit.shoeshopbackend.service.DonNhapHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donnhaphang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonNhapHangController {

    private final DonNhapHangService donNhapHangService;

    @GetMapping
    public ResponseEntity<List<DonNhapHang>> getAllDonNhapHang() {
        return ResponseEntity.ok(donNhapHangService.getAllDonNhapHang());
    }

    @GetMapping("/{maDonNhap}")
    public ResponseEntity<DonNhapHang> getDonNhapHangById(@PathVariable String maDonNhap) {
        return donNhapHangService.getDonNhapHangById(maDonNhap)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DonNhapHang> createDonNhapHang(@RequestBody DonNhapHang donNhapHang) {
        return ResponseEntity.ok(donNhapHangService.addDonNhapHang(donNhapHang));
    }

    @PutMapping("/{maDonNhap}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DonNhapHang> updateDonNhapHang(@PathVariable String maDonNhap, @RequestBody DonNhapHang donNhapHang) {
        return ResponseEntity.ok(donNhapHangService.updateDonNhapHang(maDonNhap, donNhapHang));
    }

    @DeleteMapping("/{maDonNhap}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDonNhapHang(@PathVariable String maDonNhap) {
        donNhapHangService.deleteDonNhapHang(maDonNhap);
        return ResponseEntity.noContent().build();
    }
}
