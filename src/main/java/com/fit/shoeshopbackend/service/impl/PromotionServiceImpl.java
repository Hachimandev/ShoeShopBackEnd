package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.dto.PromotionRequest;
import com.fit.shoeshopbackend.model.Promotion;
import com.fit.shoeshopbackend.model.Staff;
import com.fit.shoeshopbackend.repository.PromotionRepository;
import com.fit.shoeshopbackend.repository.StaffRepository;
import com.fit.shoeshopbackend.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final StaffRepository staffRepository;

    @Override
    public List<Promotion> getAllPromotion() {
        return promotionRepository.findAll();
    }

    @Override
    public Promotion getPromotionById(String promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    private String generateNewPromotionId() {
        String maxId = promotionRepository.findMaxPromotionId();
        int nextNumber = 1;

        if (maxId != null && maxId.startsWith("PR")) {
            try {
                String numberPart = maxId.substring(2);
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }

        DecimalFormat df = new DecimalFormat("000");
        return "PR" + df.format(nextNumber);
    }

    @Override
    @Transactional
    public Promotion addPromotion(PromotionRequest request) {
        Staff staff = null;
        if (request.getStaffId() != null) {
            staff = staffRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
        }

        String newPromotionId = generateNewPromotionId();

        Promotion promotion = Promotion.builder()
                .promotionId(newPromotionId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .condition(request.getCondition())
                .discount(request.getDiscount())
                .staff(staff)
                .build();

        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public Promotion updatePromotion(String promotionId, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setCondition(request.getCondition());
        promotion.setDiscount(request.getDiscount());

        if (request.getStaffId() != null) {
            Staff staff = staffRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new RuntimeException("Staff not found"));
            promotion.setStaff(staff);
        }

        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void deletePromotion(String promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new RuntimeException("Promotion not found for deletion");
        }
        promotionRepository.deleteById(promotionId);
    }

    @Override
    public List<Promotion> searchPromotion(String keyword) {
        return promotionRepository.findByPromotionIdContainingOrConditionContaining(keyword, keyword);
    }

    @Override
    public List<Promotion> getPromotionValid() {
        return promotionRepository.findPromotionValid(new Date());
    }
}
