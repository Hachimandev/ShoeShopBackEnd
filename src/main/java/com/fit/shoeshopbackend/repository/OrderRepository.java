package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT h FROM Order h WHERE h.orderId LIKE :prefix% ORDER BY h.orderId DESC")
    List<Order> findByIdOrderStartingWithOrderByIdOrderDesc(String prefix, Pageable pageable);

    @Query("SELECT h FROM Order h JOIN FETCH h.customer ORDER BY h.orderDate DESC")
    List<Order> findRecentWithCustomer(Pageable pageable);
}
