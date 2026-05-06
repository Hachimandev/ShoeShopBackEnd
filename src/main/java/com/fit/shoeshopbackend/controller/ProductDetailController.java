package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.model.ProductDetail;
import com.fit.shoeshopbackend.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-details")
@RequiredArgsConstructor
public class ProductDetailController {

    private final ProductDetailService productDetailService;

    @GetMapping
    public ResponseEntity<List<ProductDetail>> getAll() {
        return ResponseEntity.ok(productDetailService.getAllProductDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetail> getById(@PathVariable String id) {
        return productDetailService.getProductDetailById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDetail> create(@RequestBody ProductDetail productDetail) {
        return ResponseEntity.ok(productDetailService.addProductDetail(productDetail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetail> update(@PathVariable String id, @RequestBody ProductDetail productDetail) {
        return ResponseEntity.ok(productDetailService.updateProductDetail(id, productDetail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productDetailService.deleteProductDetail(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<ProductDetail>> getByProduct(@PathVariable String productId) {
        List<ProductDetail> details = productDetailService.getProductDetailsByProductId(productId);
        return ResponseEntity.ok(details);
    }
}
