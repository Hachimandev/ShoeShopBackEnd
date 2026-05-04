package com.fit.shoeshopbackend.controller;




import com.fit.shoeshopbackend.dto.ChangePasswordRequest;
import com.fit.shoeshopbackend.dto.UpdateAccountRequest;
import com.fit.shoeshopbackend.model.TaiKhoan;
import com.fit.shoeshopbackend.service.TaiKhoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/taikhoan")
@RequiredArgsConstructor
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;
    @GetMapping("/me/{username}")
    public ResponseEntity<TaiKhoan> getCurrentAccount(@PathVariable String username) {
        TaiKhoan taiKhoan = taiKhoanService.getCurrentAccount(username);
        return ResponseEntity.ok(taiKhoan);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateAccount(@PathVariable String username, @RequestBody UpdateAccountRequest request) {
        try {
            TaiKhoan updatedAccount = taiKhoanService.updateAccount(username, request);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change-password/{username}")
    public ResponseEntity<?> changePassword(@PathVariable String username, @RequestBody ChangePasswordRequest request) {
        try {

            taiKhoanService.changePassword(username, request);


            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
