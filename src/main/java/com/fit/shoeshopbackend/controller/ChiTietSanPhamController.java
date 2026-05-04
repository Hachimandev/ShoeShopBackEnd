package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.model.ChiTietSanPham;
import com.fit.shoeshopbackend.service.ChiTietSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-details")
@RequiredArgsConstructor
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService service;

    @GetMapping
    public ResponseEntity<List<ChiTietSanPham>> getAll() {
        return ResponseEntity.ok(service.getAllChiTietSanPham());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChiTietSanPham> getById(@PathVariable String id) {
        return service.getChiTietSanPhamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChiTietSanPham> create(@RequestBody ChiTietSanPham chiTietSanPham) {
        return ResponseEntity.ok(service.addChiTietSanPham(chiTietSanPham));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietSanPham> update(@PathVariable String id, @RequestBody ChiTietSanPham chiTietSanPham) {
        return ResponseEntity.ok(service.updateChiTietSanPham(id, chiTietSanPham));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteChiTietSanPham(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<ChiTietSanPham>> getBySanPham(@PathVariable String productId) {
        List<ChiTietSanPham> chiTiets = service.getChiTietSanPhamBySanPham(productId);
        return ResponseEntity.ok(chiTiets);
    }
}

