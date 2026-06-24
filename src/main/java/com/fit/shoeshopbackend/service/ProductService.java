package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProduct(String searchTerm, String category, String gender, String brand, List<String> sizes, String sort, Double minPrice, Double maxPrice);

    Optional<Product> getProductById(String id);

    Product addProduct(Product Product);

    Product updateProduct(String id, Product Product);

    void deleteProduct(String id);

    byte[] exportToExcel() throws java.io.IOException;
}









