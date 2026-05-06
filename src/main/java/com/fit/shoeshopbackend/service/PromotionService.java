package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.dto.PromotionRequest;
import com.fit.shoeshopbackend.model.Promotion;
import java.util.List;

public interface PromotionService {
    List<Promotion> getAllPromotion();
    Promotion getPromotionById(String promotionId);
    Promotion addPromotion(PromotionRequest request);
    Promotion updatePromotion(String promotionId, PromotionRequest request);
    void deletePromotion(String promotionId);
    List<Promotion> searchPromotion(String keyword);
    List<Promotion> getPromotionValid();
}









