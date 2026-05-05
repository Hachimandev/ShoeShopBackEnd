package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.ImportOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportOrderDetailRepository extends JpaRepository<ImportOrderDetail, String> {
    List<ImportOrderDetail> findByImportOrder_ImportOrderId(String importOrderId);
}
