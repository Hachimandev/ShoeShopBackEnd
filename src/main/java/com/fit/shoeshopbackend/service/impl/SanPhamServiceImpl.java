package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.model.ChiTietSanPham;
import com.fit.shoeshopbackend.model.GioiTinh;
import com.fit.shoeshopbackend.model.SanPham;
import com.fit.shoeshopbackend.repository.SanPhamRepository;
import com.fit.shoeshopbackend.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;


    @Override
    public List<SanPham> getAllSanPham(String searchTerm, String category, String gender, String brand, List<String> sizes, String sort, Double minPrice, Double maxPrice) {

        Specification<SanPham> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.equalsIgnoreCase("all")) {
                if (category.equalsIgnoreCase("sale")) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("thue"), 0.0));
                } else if (category.equalsIgnoreCase("sandals") || category.equalsIgnoreCase("dep")) {
                    Join<Object, Object> loaiJoin = root.join("loaiSanPham");
                    predicates.add(criteriaBuilder.like(loaiJoin.get("tenLoai"), "%Sandal%"));
                }
            }

            if (gender != null && !gender.isEmpty()) {
                try {
                    GioiTinh gioiTinh = GioiTinh.valueOf(gender);
                    predicates.add(criteriaBuilder.equal(root.get("gioiTinh"), gioiTinh));
                } catch (IllegalArgumentException e) {
                }
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String likeTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tenSanPham")), likeTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("moTa")), likeTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("thuongHieu")), likeTerm)
                );
                predicates.add(searchPredicate);
            }

            if (brand != null && !brand.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("thuongHieu"), brand));
            }

            if (sizes != null && !sizes.isEmpty()) {
                Join<SanPham, ChiTietSanPham> ctspJoin = root.join("chiTietSanPhams");
                List<Integer> sizeInts = sizes.stream().map(Integer::parseInt).toList();

                predicates.add(ctspJoin.get("size").in(sizeInts));
                query.distinct(true);
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("giaBan"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("giaBan"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sortObj = Sort.by(Sort.Direction.ASC, "maSanPham");
        if (sort != null) {
            if (sort.equalsIgnoreCase("price_low")) {
                sortObj = Sort.by(Sort.Direction.ASC, "giaBan");
            } else if (sort.equalsIgnoreCase("price_high")) {
                sortObj = Sort.by(Sort.Direction.DESC, "giaBan");
            }
        }

        return sanPhamRepository.findAll(spec, sortObj);
    }

    @Override
    public Optional<SanPham> getSanPhamById(String id) {
        return sanPhamRepository.findById(id);
    }



    ///  them sản phẩm

    // phát sinh mã sản phẩm
    public String phatSinhMaSP(){
        return "SP" + System.currentTimeMillis();
    }

    // phát sinh mã chi tiết sản phẩm
    public String phatSinhMaCTSP(){
        return "SP" + System.nanoTime();
    }

    @Override
    public SanPham addSanPham(SanPham sanPham) {
        sanPham.setMaSanPham(phatSinhMaSP());
        if (sanPham.getChiTietSanPhams() != null) {
            for (ChiTietSanPham ct : sanPham.getChiTietSanPhams()) {
                ct.setMaChiTiet(phatSinhMaCTSP());
                ct.setSanPham(sanPham);
            }
        }


        return sanPhamRepository.save(sanPham);
    }


    @Override
    public SanPham updateSanPham(String id, SanPham sanPham) {
        sanPham.setMaSanPham(id);
        return sanPhamRepository.save(sanPham);
    }

    @Override
    public void deleteSanPham(String id) {
        sanPhamRepository.deleteById(id);
    }
}

