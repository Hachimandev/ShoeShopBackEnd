package com.fit.shoeshopbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnOrderDetail {
    @Id
    private String returnOrderDetailId;
    private int quantity;
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "returnOrderId")
    private ReturnOrder returnOrder;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "productDetailId")
    private ProductDetail productDetail;
}
