package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.ImportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportOrderRepository extends JpaRepository<ImportOrder, String> {
}










