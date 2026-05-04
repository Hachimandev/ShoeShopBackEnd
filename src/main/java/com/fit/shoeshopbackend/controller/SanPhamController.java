package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.model.SanPham;
import com.fit.shoeshopbackend.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;


    @GetMapping
    public ResponseEntity<List<SanPham>> getAllSanPham(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String gender
    ) {
        return ResponseEntity.ok(sanPhamService.getAllSanPham(searchTerm, category, gender, brand, sizes, sort, minPrice, maxPrice));
    }


    @GetMapping("/{id}")
    public ResponseEntity<SanPham> getSanPhamById(@PathVariable String id) {
        return sanPhamService.getSanPhamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<SanPham> createSanPham(@RequestBody SanPham sanPham) {
        return ResponseEntity.ok(sanPhamService.addSanPham(sanPham));
    }

    @PutMapping("/{id}")

    public ResponseEntity<SanPham> updateSanPham(@PathVariable String id, @RequestBody SanPham sanPham) {
        return ResponseEntity.ok(sanPhamService.updateSanPham(id, sanPham));
    }


    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteSanPham(@PathVariable String id) {
        sanPhamService.deleteSanPham(id);
        return ResponseEntity.noContent().build();
    }
}

