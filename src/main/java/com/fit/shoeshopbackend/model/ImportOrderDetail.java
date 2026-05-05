package com.fit.shoeshopbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportOrderDetail {
    @Id
    private String importOrderDetailId;
    private int quantity;
    private double importPrice;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "importOrderId")
    private ImportOrder importOrder;
}
