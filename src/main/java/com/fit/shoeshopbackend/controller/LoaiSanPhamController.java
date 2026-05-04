package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.model.LoaiSanPham;
import com.fit.shoeshopbackend.service.LoaiSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class LoaiSanPhamController {

    private final LoaiSanPhamService service;

    @GetMapping
    public ResponseEntity<List<LoaiSanPham>> getAll() {
        return ResponseEntity.ok(service.getAllLoaiSanPham());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoaiSanPham> getById(@PathVariable String id) {
        return service.getLoaiSanPhamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LoaiSanPham> create(@RequestBody LoaiSanPham loaiSanPham) {
        return ResponseEntity.ok(service.addLoaiSanPham(loaiSanPham));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoaiSanPham> update(@PathVariable String id, @RequestBody LoaiSanPham loaiSanPham) {
        return ResponseEntity.ok(service.updateLoaiSanPham(id, loaiSanPham));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteLoaiSanPham(id);
        return ResponseEntity.noContent().build();
    }
}

