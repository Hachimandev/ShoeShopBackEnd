package com.fit.shoeshopbackend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSummary {
    private double subtotal;
    private double shippingFee;
    private double discountPromo;
    private double discountPoints;
    private double total;
    private int customerPoints;

    public CartSummary(double subtotal, double discount, double total) {
        this.subtotal = subtotal;
        this.discountPromo = discount; // Gán discount vào trường khuyến mãi
        this.total = total;
    }
}
