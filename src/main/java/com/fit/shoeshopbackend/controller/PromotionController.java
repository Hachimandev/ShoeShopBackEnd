package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.PromotionRequest;
import com.fit.shoeshopbackend.model.Promotion;
import com.fit.shoeshopbackend.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotion());
    }

    @GetMapping("/{promotionId}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable String promotionId) {
        try {
            return ResponseEntity.ok(promotionService.getPromotionById(promotionId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addPromotion(@RequestBody PromotionRequest request) {
        try {
            Promotion promotion = promotionService.addPromotion(request);
            return ResponseEntity.status(201).body(promotion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{promotionId}")
    public ResponseEntity<?> updatePromotion(@PathVariable String promotionId, @RequestBody PromotionRequest request) {
        try {
            Promotion promotion = promotionService.updatePromotion(promotionId, request);
            return ResponseEntity.ok(promotion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{promotionId}")
    public ResponseEntity<?> deletePromotion(@PathVariable String promotionId) {
        try {
            promotionService.deletePromotion(promotionId);
            return ResponseEntity.ok().body("Promotion deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Promotion>> searchPromotion(@RequestParam String keyword) {
        List<Promotion> result = promotionService.searchPromotion(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/valid")
    public ResponseEntity<List<Promotion>> getValidPromotions() {
        List<Promotion> result = promotionService.getPromotionValid();
        return ResponseEntity.ok(result);
    }
}
