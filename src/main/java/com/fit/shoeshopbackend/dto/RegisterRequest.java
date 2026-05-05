package com.fit.shoeshopbackend.dto;



import lombok.*;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
}
