package com.fit.shoeshopbackend.config;


import com.fit.shoeshopbackend.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired private JwtAuthenticationFilter jwtFilter;
    @Autowired private AccountDetailsService userDetailsService;
    @Autowired private RateLimitingFilter rateLimitingFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // Public GET endpoints
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/promotions/**").permitAll()
                        
                        // Authenticated user endpoints
                        .requestMatchers("/api/accounts/me/**").authenticated()
                        .requestMatchers("/api/accounts/update/**").authenticated()
                        .requestMatchers("/api/accounts/change-password/**").authenticated()
                        .requestMatchers("/api/orders/checkout").authenticated()
                        .requestMatchers("/api/customers/points/**").authenticated()
                        .requestMatchers("/api/customers/info/**").authenticated()
                        .requestMatchers("/api/customers/update/**").authenticated()
                        .requestMatchers("/api/orders/**").authenticated()
                        
                        // Admin-only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/staffs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/suppliers/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/promotions/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/customers/list/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/customers/search/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/customers/stats/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/customers/export/**").hasAuthority("ROLE_ADMIN")
                        
                        .requestMatchers("/api/ai/**").permitAll()
                        .requestMatchers("/api/payment/**").permitAll() // Cho phép Webhook SePay truy cập không cần token
                        .requestMatchers("/h2-console/**").permitAll()

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        http.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        // Allow dev frontend with pattern matching (no credentials needed for public API)
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "https://shoe-shop-front-end-flax.vercel.app"
        ));
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "https://shoe-shop-front-end-flax.vercel.app",
                "https://my.sepay.vn",
                "https://*.sepay.vn"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}