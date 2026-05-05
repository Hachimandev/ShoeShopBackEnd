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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account tk = AccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài Warehouseản: " + username));
        return new AccountDetails(tk);
    }
}









