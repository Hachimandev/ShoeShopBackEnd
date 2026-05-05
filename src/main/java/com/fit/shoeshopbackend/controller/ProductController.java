package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.model.Product;
import com.fit.shoeshopbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService ProductService;


    @GetMapping
    public ResponseEntity<List<Product>> getAllProduct(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String gender
    ) {
        return ResponseEntity.ok(ProductService.getAllProduct(searchTerm, category, gender, brand, sizes, sort, minPrice, maxPrice));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ProductService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product Product) {
        return ResponseEntity.ok(ProductService.addProduct(Product));
    }

    @PutMapping("/{id}")

    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product Product) {
        return ResponseEntity.ok(ProductService.updateProduct(id, Product));
    }


    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        ProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}










