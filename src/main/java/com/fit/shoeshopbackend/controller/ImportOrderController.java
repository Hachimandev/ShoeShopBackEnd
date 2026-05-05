package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.model.ImportOrder;
import com.fit.shoeshopbackend.service.ImportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/import-orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImportOrderController {

    private final ImportOrderService importOrderService;

    @GetMapping
    public ResponseEntity<List<ImportOrder>> getAllImportOrders() {
        return ResponseEntity.ok(importOrderService.getAllImportOrder());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportOrder> getImportOrderById(@PathVariable String id) {
        return importOrderService.getImportOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportOrder> createImportOrder(@RequestBody ImportOrder importOrder) {
        return ResponseEntity.ok(importOrderService.addImportOrder(importOrder));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportOrder> updateImportOrder(@PathVariable String id, @RequestBody ImportOrder importOrder) {
        return ResponseEntity.ok(importOrderService.updateImportOrder(id, importOrder));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImportOrder(@PathVariable String id) {
        importOrderService.deleteImportOrder(id);
        return ResponseEntity.noContent().build();
    }
}
