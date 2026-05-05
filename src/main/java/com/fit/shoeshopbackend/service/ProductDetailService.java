package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.model.ProductDetail;

import java.util.List;
import java.util.Optional;

public interface ProductDetailService {
    List<ProductDetail> getAllProductDetails();

    Optional<ProductDetail> getProductDetailById(String id);

    ProductDetail addProductDetail(ProductDetail productDetail);

    ProductDetail updateProductDetail(String id, ProductDetail productDetail);

    void deleteProductDetail(String id);

    List<ProductDetail> getProductDetailsByProductId(String productId);
}
