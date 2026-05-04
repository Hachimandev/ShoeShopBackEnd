package com.fit.shoeshopbackend;

import com.fit.shoeshopbackend.config.DotenvConfig;
import com.fit.shoeshopbackend.config.JwtUtil;
import com.fit.shoeshopbackend.config.TaiKhoanDetails;
import com.fit.shoeshopbackend.model.Role;
import com.fit.shoeshopbackend.model.TaiKhoan;
import com.fit.shoeshopbackend.repository.TaiKhoanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class ShoeShopBackEndApplication {

    public static void main(String[] args) {
        DotenvConfig.dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        SpringApplication.run(ShoeShopBackEndApplication.class, args);
    }
    @Bean
    CommandLineRunner initAdmin(TaiKhoanRepository repo, PasswordEncoder encoder, JwtUtil jwtUtil) {
        return args -> {
            TaiKhoan admin;

            if (!repo.existsByTenDangNhap("admin")) {
                admin = TaiKhoan.builder()
                        .maTaiKhoan("TK001")
                        .tenDangNhap("admin")
                        .matKhau(encoder.encode("admin123"))
                        .email("admin@example.com")
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                        .build();
                repo.save(admin);
            } else {
                admin = repo.findByTenDangNhap("admin").get();
            }

            TaiKhoanDetails details = new TaiKhoanDetails(admin);
            String token = jwtUtil.generateToken(details);

            System.out.println("JWT Test: " + token);
        };
    }

}
