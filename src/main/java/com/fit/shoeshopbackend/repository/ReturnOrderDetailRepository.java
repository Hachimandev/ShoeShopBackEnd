package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.ReturnOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnOrderDetailRepository extends JpaRepository<ReturnOrderDetail, String> {
}
