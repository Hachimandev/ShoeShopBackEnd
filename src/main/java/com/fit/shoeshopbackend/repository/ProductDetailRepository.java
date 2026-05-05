package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, String> {
    List<ProductDetail> findByProduct_ProductId(String productId);
}
