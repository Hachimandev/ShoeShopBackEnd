package com.fit.shoeshopbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetail {
    @Id
    private String productDetailId;
    private String color;
    private int size;
    private int stockQuantity;

    @ManyToOne
    @JoinColumn(name = "productId")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Product product;
}
