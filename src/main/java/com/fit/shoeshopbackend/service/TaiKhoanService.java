package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.dto.ChangePasswordRequest;
import com.fit.shoeshopbackend.dto.UpdateAccountRequest;
import com.fit.shoeshopbackend.model.TaiKhoan;

public interface TaiKhoanService {
    TaiKhoan getCurrentAccount(String username);
    TaiKhoan updateAccount(String username, UpdateAccountRequest request);
    void changePassword(String username, ChangePasswordRequest request);
}