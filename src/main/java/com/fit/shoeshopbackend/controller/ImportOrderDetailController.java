package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.model.ImportOrderDetail;
import com.fit.shoeshopbackend.service.ImportOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/import-order-details")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImportOrderDetailController {

    private final ImportOrderDetailService importOrderDetailService;

    @GetMapping
    public ResponseEntity<List<ImportOrderDetail>> getAllImportOrderDetails() {
        return ResponseEntity.ok(importOrderDetailService.getAllImportOrderDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportOrderDetail> getImportOrderDetailById(@PathVariable String id) {
        return importOrderDetailService.getImportOrderDetailById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-import-order/{importOrderId}")
    public ResponseEntity<List<ImportOrderDetail>> getByImportOrder(@PathVariable String importOrderId) {
        return ResponseEntity.ok(importOrderDetailService.getImportOrderDetailsByImportOrderId(importOrderId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportOrderDetail> createImportOrderDetail(@RequestBody ImportOrderDetail detail) {
        return ResponseEntity.ok(importOrderDetailService.addImportOrderDetail(detail));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportOrderDetail> updateImportOrderDetail(@PathVariable String id, @RequestBody ImportOrderDetail detail) {
        return ResponseEntity.ok(importOrderDetailService.updateImportOrderDetail(id, detail));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImportOrderDetail(@PathVariable String id) {
        importOrderDetailService.deleteImportOrderDetail(id);
        return ResponseEntity.noContent().build();
    }
}
