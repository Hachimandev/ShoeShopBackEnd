package com.fit.shoeshopbackend.dto;


import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private List<CartItemDTO> items = new ArrayList<>();
    private String promotionId;
    private int usedPoints;

    public Cart() {}

    public Cart(List<CartItemDTO> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public void addItem(CartItemDTO newItem, int stockQuantity) {
        boolean found = false;
        for (CartItemDTO item : items) {
            if (item.getProductDetailId().equals(newItem.getProductDetailId())) {
                int updatedQty = item.getQuantity() + newItem.getQuantity();
                item.setQuantity(Math.min(updatedQty, stockQuantity));
                found = true;
                break;
            }
        }
        if (!found) {
            newItem.setQuantity(Math.min(newItem.getQuantity(), stockQuantity));
            items.add(newItem);
        }
    }

    public void updateQuantity(String productDetailId, int quantity, int stockQuantity) {
        for (CartItemDTO item : items) {
            if (item.getProductDetailId().equals(productDetailId)) {
                item.setQuantity(Math.min(quantity, stockQuantity));
                break;
            }
        }
    }

    public void removeItem(String productDetailId) {
        items.removeIf(item -> item.getProductDetailId().equals(productDetailId));
    }

    public void clear() {
        items.clear();
        promotionId = null;
        usedPoints = 0;
    }
}









