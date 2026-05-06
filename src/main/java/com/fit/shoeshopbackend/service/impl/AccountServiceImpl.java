package com.fit.shoeshopbackend.service.impl;




import com.fit.shoeshopbackend.dto.ChangePasswordRequest;
import com.fit.shoeshopbackend.dto.UpdateAccountRequest;
import com.fit.shoeshopbackend.model.Customer;
import com.fit.shoeshopbackend.model.Account;
import com.fit.shoeshopbackend.repository.AccountRepository;
import com.fit.shoeshopbackend.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository AccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account getCurrentAccount(String username) {
        return AccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài Warehouseản"));
    }

    @Override
    @Transactional
    public Account updateAccount(String username, UpdateAccountRequest request) {
        Account Account = AccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài Warehouseản"));

        if (request.getEmail() != null && !request.getEmail().equals(Account.getEmail())) {
            if (AccountRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng");
            }
            Account.setEmail(request.getEmail());
        }

        Customer Customer = Account.getCustomer();

        if (Customer != null) {
            if (request.getFullName() != null) {
                Customer.setFullName(request.getFullName());
            }
            if (request.getPhoneNumber() != null) {
                Customer.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getAddress() != null) {
                Customer.setAddress(request.getAddress());
            }
        } else if (request.getFullName() != null || request.getPhoneNumber() != null || request.getAddress() != null) {
        }

        return AccountRepository.save(Account);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        Account Account = AccountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài Warehouseản"));

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        if (oldPassword == null || !passwordEncoder.matches(oldPassword, Account.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }

        if (passwordEncoder.matches(newPassword, Account.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        Account.setPassword(passwordEncoder.encode(newPassword));
        AccountRepository.save(Account);

    }
}









