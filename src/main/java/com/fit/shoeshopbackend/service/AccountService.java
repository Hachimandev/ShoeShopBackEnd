package com.fit.shoeshopbackend.service;


import com.fit.shoeshopbackend.dto.ChangePasswordRequest;
import com.fit.shoeshopbackend.dto.UpdateAccountRequest;
import com.fit.shoeshopbackend.model.Account;

public interface AccountService {
    Account getCurrentAccount(String username);
    Account updateAccount(String username, UpdateAccountRequest request);
    void changePassword(String username, ChangePasswordRequest request);
}








