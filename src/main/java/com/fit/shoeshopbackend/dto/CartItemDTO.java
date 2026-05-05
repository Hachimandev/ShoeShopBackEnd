package com.fit.shoeshopbackend.dto;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private String productDetailId;
    private Integer quantity;
    private String productName;
    private Double price;
    private Integer size;
    private String color;
    private Integer stockQuantity;
    private String image;
}










