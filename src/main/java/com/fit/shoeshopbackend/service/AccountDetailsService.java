package com.fit.shoeshopbackend.service;
import com.fit.shoeshopbackend.config.AccountDetails;
import com.fit.shoeshopbackend.model.Account;
import com.fit.shoeshopbackend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository AccountRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Account tk = AccountRepository.findByUsername(usernameOrEmail)
                .or(() -> AccountRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với tên đăng nhập hoặc email: " + usernameOrEmail));
        return new AccountDetails(tk);
    }
}









