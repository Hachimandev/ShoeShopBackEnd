package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.ProductDetail;
import com.fit.shoeshopbackend.repository.ProductDetailRepository;
import com.fit.shoeshopbackend.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductDetailRepository productDetailRepository;

    @Override
    public List<ProductDetail> getAllProductDetails() {
        return productDetailRepository.findAll();
    }

    @Override
    public Optional<ProductDetail> getProductDetailById(String id) {
        return productDetailRepository.findById(id);
    }

    @Override
    public ProductDetail addProductDetail(ProductDetail productDetail) {
        return productDetailRepository.save(productDetail);
    }

    @Override
    public ProductDetail updateProductDetail(String id, ProductDetail productDetail) {
        productDetail.setProductDetailId(id);
        return productDetailRepository.save(productDetail);
    }

    @Override
    public void deleteProductDetail(String id) {
        productDetailRepository.deleteById(id);
    }

    @Override
    public List<ProductDetail> getProductDetailsByProductId(String productId) {
        return productDetailRepository.findByProduct_ProductId(productId);
    }
}
