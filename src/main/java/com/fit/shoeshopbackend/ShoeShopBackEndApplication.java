package com.fit.shoeshopbackend;

import com.fit.shoeshopbackend.config.DotenvConfig;
import com.fit.shoeshopbackend.config.JwtUtil;
import com.fit.shoeshopbackend.config.AccountDetails;
import com.fit.shoeshopbackend.model.Role;
import com.fit.shoeshopbackend.model.Account;
import com.fit.shoeshopbackend.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication(scanBasePackages = "com.fit")
@EnableCaching
public class ShoeShopBackEndApplication {

    public static void main(String[] args) {
        DotenvConfig.dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        SpringApplication.run(ShoeShopBackEndApplication.class, args);
    }
    @Bean
    CommandLineRunner initAdmin(AccountRepository repo, PasswordEncoder encoder, JwtUtil jwtUtil) {
        return args -> {
            Account admin;

            if (!repo.existsByUsername("admin")) {
                admin = Account.builder()
                        .accountId("TK001")
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .email("admin@example.com")
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                        .build();
                repo.save(admin);
            } else {
                admin = repo.findByUsername("admin").get();
            }

            AccountDetails details = new AccountDetails(admin);
            String token = jwtUtil.generateToken(details);

            System.out.println("JWT Test: " + token);
        };
    }

}









