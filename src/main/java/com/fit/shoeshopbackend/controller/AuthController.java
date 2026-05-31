package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.config.JwtUtil;
import com.fit.shoeshopbackend.config.AccountDetails;
import com.fit.shoeshopbackend.dto.AuthRequest;
import com.fit.shoeshopbackend.dto.AuthResponse;
import com.fit.shoeshopbackend.dto.GoogleLoginRequest;
import com.fit.shoeshopbackend.dto.RegisterRequest;
import com.fit.shoeshopbackend.model.Customer;
import com.fit.shoeshopbackend.model.Role;
import com.fit.shoeshopbackend.model.Account;
import com.fit.shoeshopbackend.repository.CustomerRepository;
import com.fit.shoeshopbackend.repository.AccountRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AccountRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CustomerRepository khachHangRepository;
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private com.fit.shoeshopbackend.service.EmailService emailService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        var userDetails = (AccountDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);


        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new AuthResponse(token, userDetails.getUsername(), roles);
    }

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email không được trống.");
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body("Định dạng email không hợp lệ.");
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email đã tồn tại trong hệ thống.");
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
        redisTemplate.opsForValue().set("otp:" + email, otp, 60, TimeUnit.SECONDS);

        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi đến email của bạn."));
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        if (email == null || email.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email và mã OTP không được trống.");
        }

        String cachedOtp = redisTemplate.opsForValue().get("otp:" + email);
        if (cachedOtp == null) {
            return ResponseEntity.badRequest().body("Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng gửi lại.");
        }

        if (!cachedOtp.equals(otp)) {
            return ResponseEntity.badRequest().body("Mã OTP không chính xác.");
        }

        redisTemplate.opsForValue().set("email_verified:" + email, "true", 300, TimeUnit.SECONDS);
        redisTemplate.delete("otp:" + email);

        return ResponseEntity.ok(Map.of("message", "Xác thực email thành công."));
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r) {
        String isVerified = redisTemplate.opsForValue().get("email_verified:" + r.getEmail());
        if (isVerified == null || !isVerified.equals("true")) {
            return ResponseEntity.badRequest().body("Phiên đăng ký đã hết hạn hoặc chưa xác thực email. Vui lòng thực hiện lại từ đầu.");
        }

        if (userRepository.existsByUsername(r.getUsername())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại.");
        }
        if (r.getEmail() != null && userRepository.existsByEmail(r.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại.");
        }

        if (r.getPassword() == null || r.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("Mật khẩu phải có độ dài tối thiểu 8 ký tự.");
        }

        if (r.getConfirmPassword() == null || !r.getConfirmPassword().equals(r.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp.");
        }

        Account user = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .username(r.getUsername())
                .email(r.getEmail())
                .password(passwordEncoder.encode(r.getPassword()))
                .roles(Set.of(Role.ROLE_USER))
                .build();

        Customer kh = Customer.builder()
                .customerId(UUID.randomUUID().toString())
                .fullName(r.getFullName())
                .email(r.getEmail())
                .phoneNumber(null)
                .address(null)
                .loyaltyPoints(0)
                .joinDate(LocalDateTime.now())
                .totalSpending(0)
                .account(user)
                .build();

        user.setCustomer(kh);
        userRepository.save(user); // cascade ALL sẽ tự lưu Customer

        redisTemplate.delete("email_verified:" + r.getEmail());

        return ResponseEntity.ok("Registered");
    }


    @PostMapping("/login-google")
    public AuthResponse loginWithGoogle(@RequestBody GoogleLoginRequest req) {

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).setAudience(List.of("586282939098-ju8to28c5rspseash1t3cng4r6d1ursl.apps.googleusercontent.com", "586282939098-61fm5vqcb51lc312l502b7j8t7oukiv8.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(req.getIdToken());
            if (idToken == null) {
                throw new RuntimeException("Token Google không hợp lệ");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String fullName = (String) payload.get("name");

            // --- kiểm tra tài khoản ---
            Account user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // --- tạo tài khoản mới ---
                user = Account.builder()
                        .accountId(UUID.randomUUID().toString())
                        .username(email)
                        .email(email)
                        .password(passwordEncoder.encode("GOOGLE_USER"))
                        .roles(Set.of(Role.ROLE_USER))
                        .build();

                Customer kh = Customer.builder()
                        .customerId(UUID.randomUUID().toString())
                        .fullName(fullName)
                        .email(email)
                        .loyaltyPoints(0)
                        .joinDate(LocalDateTime.now())
                        .totalSpending(0)
                        .account(user)
                        .build();

                user.setCustomer(kh);
                userRepository.save(user);
            }

            // --- tạo JWT ---
            AccountDetails details = new AccountDetails(user);
            String token = jwtUtil.generateToken(details);

            List<String> roles = user.getRoles().stream().map(Enum::name).toList();

            return new AuthResponse(token, user.getUsername(), roles);

        } catch (Exception e) {
            throw new RuntimeException("Login Google thất bại: " + e.getMessage());
        }
    }

}
