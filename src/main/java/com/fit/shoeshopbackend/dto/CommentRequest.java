package com.fit.shoeshopbackend.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String productId;
    private String username;
    private String content;
    private int rating;
}









