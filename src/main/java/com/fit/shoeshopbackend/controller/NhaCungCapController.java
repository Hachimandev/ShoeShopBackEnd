package com.fit.shoeshopbackend.controller;



import java.io.IOException;
import java.util.List;

import com.fit.shoeshopbackend.model.NhaCungCap;
import com.fit.shoeshopbackend.service.NhaCungCapService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class NhaCungCapController {

    private final NhaCungCapService service;

    @GetMapping
    public ResponseEntity<List<NhaCungCap>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhaCungCap> getNhaCungCapById(@PathVariable String id) {
        return service.getNhaCungCapById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NhaCungCap> createNhaCungCap(@RequestBody NhaCungCap nhaCungCap) {
        return ResponseEntity.ok(service.addNhaCungCap(nhaCungCap));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhaCungCap> updateNhaCungCap(
            @PathVariable String id,
            @RequestBody NhaCungCap nhaCungCap
    ) {
        return ResponseEntity.ok(service.updateNhaCungCap(id, nhaCungCap));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNhaCungCap(@PathVariable String id) {
        service.deleteNhaCungCap(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<NhaCungCap>> searchSuppliers(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchSuppliers(keyword));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        byte[] excelFile = service.exportToExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=NhaCungCap.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }
}