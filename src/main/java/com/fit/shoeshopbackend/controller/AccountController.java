package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.ChangePasswordRequest;
import com.fit.shoeshopbackend.dto.UpdateAccountRequest;
import com.fit.shoeshopbackend.model.Account;
import com.fit.shoeshopbackend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me/{username}")
    public ResponseEntity<Account> getCurrentAccount(@PathVariable String username) {
        Account account = accountService.getCurrentAccount(username);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateAccount(@PathVariable String username, @RequestBody UpdateAccountRequest request) {
        try {
            Account updatedAccount = accountService.updateAccount(username, request);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change-password/{username}")
    public ResponseEntity<?> changePassword(@PathVariable String username, @RequestBody ChangePasswordRequest request) {
        try {
            accountService.changePassword(username, request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
