package com.fit.shoeshopbackend.repository;

import com.fit.shoeshopbackend.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {

    @Query("SELECT k FROM Promotion k WHERE k.startDate <= :currentDate AND k.endDate >= :currentDate")
    List<Promotion> findPromotionValid(@Param("currentDate") Date currentDate);

    List<Promotion> findByPromotionIdContainingOrConditionContaining(String promotionId, String condition);

    @Query(value = "SELECT promotionId FROM Promotion ORDER BY promotionId DESC LIMIT 1")
    String findMaxPromotionId();
}
