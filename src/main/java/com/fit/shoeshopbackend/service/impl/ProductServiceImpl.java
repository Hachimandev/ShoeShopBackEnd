package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.ProductDetail;
import com.fit.shoeshopbackend.model.Gender;
import com.fit.shoeshopbackend.model.Product;
import com.fit.shoeshopbackend.repository.ProductRepository;
import com.fit.shoeshopbackend.service.ProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "{#searchTerm, #category, #gender, #brand, #sizes, #sort, #minPrice, #maxPrice}")
    public List<Product> getAllProduct(String searchTerm, String category, String gender, String brand, List<String> sizes, String sort, Double minPrice, Double maxPrice) {

        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.equalsIgnoreCase("all")) {
                if (category.equalsIgnoreCase("sale")) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("tax"), 0.0));
                } else {
                    Join<Object, Object> categoryJoin = root.join("category");
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("categoryName")), "%" + category.toLowerCase() + "%"));
                }
            }

            if (gender != null && !gender.isEmpty()) {
                try {
                    Gender genderEnum = Gender.valueOf(gender);
                    predicates.add(criteriaBuilder.equal(root.get("gender"), genderEnum));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid gender
                }
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likeTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), likeTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likeTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), likeTerm)
                );
                predicates.add(searchPredicate);
            }

            if (brand != null && !brand.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand"), brand));
            }

            if (sizes != null && !sizes.isEmpty()) {
                Join<Product, ProductDetail> detailJoin = root.join("productDetails");
                List<Integer> sizeInts = sizes.stream().map(Integer::parseInt).toList();
                predicates.add(detailJoin.get("size").in(sizeInts));
                query.distinct(true);
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sortObj = Sort.by(Sort.Direction.ASC, "productId");
        if (sort != null) {
            if (sort.equalsIgnoreCase("price_low")) {
                sortObj = Sort.by(Sort.Direction.ASC, "price");
            } else if (sort.equalsIgnoreCase("price_high")) {
                sortObj = Sort.by(Sort.Direction.DESC, "price");
            } else if (sort.equalsIgnoreCase("best_selling")) {
                sortObj = Sort.by(Sort.Direction.DESC, "soldQuantity");
            } else if (sort.equalsIgnoreCase("newest")) {
                sortObj = Sort.by(Sort.Direction.DESC, "productId"); // Assuming productId is generated sequentially or by time
            }
        }

        List<Product> products = productRepository.findAll(spec, sortObj);
        // Eagerly initialize productDetails to cache them
        products.forEach(p -> {
            if (p.getProductDetails() != null) {
                p.getProductDetails().size();
            }
        });
        return products;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public Optional<Product> getProductById(String id) {
        Optional<Product> productOpt = productRepository.findById(id);
        productOpt.ifPresent(p -> {
            if (p.getProductDetails() != null) {
                p.getProductDetails().size();
            }
        });
        return productOpt;
    }

    private String generateProductId() {
        return "PRD" + System.currentTimeMillis();
    }

    private String generateProductDetailId() {
        return "DTL" + System.nanoTime();
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product addProduct(Product product) {
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            product.setProductId(generateProductId());
        }
        if (product.getProductDetails() != null) {
            for (ProductDetail detail : product.getProductDetails()) {
                if (detail.getProductDetailId() == null || detail.getProductDetailId().isEmpty()) {
                    detail.setProductDetailId(generateProductDetailId());
                }
                detail.setProduct(product);
            }
        }
        return productRepository.save(product);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "product", key = "#id"),
        @CacheEvict(value = "products", allEntries = true)
    })
    public Product updateProduct(String id, Product product) {
        product.setProductId(id);
        return productRepository.save(product);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "product", key = "#id"),
        @CacheEvict(value = "products", allEntries = true)
    })
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
