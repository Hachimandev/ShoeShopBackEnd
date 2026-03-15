package com.fit.shoeshopbackend;

import com.fit.shoeshopbackend.config.DotenvConfig;
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
    CommandLineRunner initAdmin(TaiKhoanRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (!repo.existsByTenDangNhap("admin")) {
                TaiKhoan admin = TaiKhoan.builder()
                        .maTaiKhoan("TK001")
                        .tenDangNhap("admin")
                        .matKhau(encoder.encode("admin123"))
                        .email("admin@example.com")
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                        .build();
                repo.save(admin);
                System.out.println("Admin created with username=admin password=admin123");
            }
        };
    }

}
