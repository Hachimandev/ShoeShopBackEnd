package com.fit.shoeshopbackend.service;
import com.fit.shoeshopbackend.config.TaiKhoanDetails;
import com.fit.shoeshopbackend.model.TaiKhoan;
import com.fit.shoeshopbackend.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class TaiKhoanDetailsService implements UserDetailsService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));
        return new TaiKhoanDetails(tk);
    }
}
