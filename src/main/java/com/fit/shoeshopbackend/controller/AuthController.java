package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.config.JwtUtil;
import com.fit.shoeshopbackend.config.AccountDetails;
import com.fit.shoeshopbackend.dto.AuthRequest;
import com.fit.shoeshopbackend.dto.AuthResponse;
import com.fit.shoeshopbackend.dto.GoogleLoginRequest;
import com.fit.shoeshopbackend.dto.RefreshTokenRequest;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Value("${google.oauth.client-ids:${GOOGLE_CLIENT_ID:}}")
    private String googleClientIds;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            var userDetails = (AccountDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken();
            redisTemplate.opsForValue().set("refresh_token:" + refreshToken, userDetails.getUsername(), 7, TimeUnit.DAYS);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            Account user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            return ResponseEntity.ok(new AuthResponse(token, refreshToken, userDetails.getUsername(), user.getAccountId(), roles));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Tên đăng nhập hoặc mật khẩu không đúng."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage()));
        }
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


    @Transactional
    @PostMapping("/login-google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest req) {

        try {
            if (req.getIdToken() == null || req.getIdToken().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Google ID token is required."));
            }

            List<String> audiences = Arrays.stream(googleClientIds.split(","))
                    .map(String::trim)
                    .filter(clientId -> !clientId.isBlank())
                    .toList();

            if (audiences.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of("error", "Google OAuth client id is not configured."));
            }

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).setAudience(audiences)
                    .build();

            GoogleIdToken idToken = verifier.verify(req.getIdToken());
            if (idToken == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Google token is invalid."));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                return ResponseEntity.status(401).body(Map.of("error", "Google email is not verified."));
            }

            String email = payload.getEmail().trim().toLowerCase();
            String fullName = (String) payload.get("name");

            // --- kiểm tra tài khoản ---
            Account user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // --- tạo tài khoản mới ---
                user = Account.builder()
                        .accountId(UUID.randomUUID().toString())
                        .username(email)
                        .email(email)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
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
            String refreshToken = jwtUtil.generateRefreshToken();
            redisTemplate.opsForValue().set("refresh_token:" + refreshToken, user.getUsername(), 7, TimeUnit.DAYS);

            List<String> roles = user.getRoles().stream().map(Enum::name).toList();

            return ResponseEntity.ok(new AuthResponse(token, refreshToken, user.getUsername(), user.getAccountId(), roles));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Google login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is missing."));
        }

        String username = redisTemplate.opsForValue().get("refresh_token:" + requestRefreshToken);
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired refresh token."));
        }

        Account user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found."));
        }

        AccountDetails userDetails = new AccountDetails(user);
        String newToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken();

        // Xóa token cũ và lưu token mới
        redisTemplate.delete("refresh_token:" + requestRefreshToken);
        redisTemplate.opsForValue().set("refresh_token:" + newRefreshToken, username, 7, TimeUnit.DAYS);

        List<String> roles = user.getRoles().stream().map(Enum::name).toList();

        return ResponseEntity.ok(new AuthResponse(newToken, newRefreshToken, username, user.getAccountId(), roles));
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> forgotPasswordSendOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email không được trống.");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.badRequest().body("Định dạng email không hợp lệ.");
        }

        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email không tồn tại trong hệ thống.");
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
        redisTemplate.opsForValue().set("forgot_password_otp:" + email, otp, 60, TimeUnit.SECONDS);

        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of("message", "Mã OTP phục hồi đã được gửi đến email của bạn."));
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> forgotPasswordVerifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        if (email == null || email.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email và mã OTP không được trống.");
        }

        String cachedOtp = redisTemplate.opsForValue().get("forgot_password_otp:" + email);
        if (cachedOtp == null) {
            return ResponseEntity.badRequest().body("Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng gửi lại.");
        }

        if (!cachedOtp.equals(otp)) {
            return ResponseEntity.badRequest().body("Mã OTP không chính xác.");
        }

        redisTemplate.opsForValue().set("forgot_password_verified:" + email, "true", 300, TimeUnit.SECONDS);
        redisTemplate.delete("forgot_password_otp:" + email);

        return ResponseEntity.ok(Map.of("message", "Xác thực OTP thành công."));
    }

    @Transactional
    @PostMapping("/forgot-password/reset-password")
    public ResponseEntity<?> forgotPasswordResetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        String confirmPassword = payload.get("confirmPassword");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email và mật khẩu không được trống.");
        }

        String isVerified = redisTemplate.opsForValue().get("forgot_password_verified:" + email);
        if (isVerified == null || !isVerified.equals("true")) {
            return ResponseEntity.badRequest().body("Phiên xác thực đã hết hạn hoặc chưa xác thực OTP. Vui lòng thử lại.");
        }

        if (password.length() < 8) {
            return ResponseEntity.badRequest().body("Mật khẩu phải có độ dài tối thiểu 8 ký tự.");
        }

        if (confirmPassword == null || !confirmPassword.equals(password)) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp.");
        }

        Account account = userRepository.findByEmail(email)
                .orElse(null);
        if (account == null) {
            return ResponseEntity.badRequest().body("Tài khoản không tồn tại.");
        }

        account.setPassword(passwordEncoder.encode(password));
        userRepository.save(account);

        redisTemplate.delete("forgot_password_verified:" + email);

        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công."));
    }
}
