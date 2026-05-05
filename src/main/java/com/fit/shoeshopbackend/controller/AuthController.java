package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.config.JwtUtil;
import com.fit.shoeshopbackend.config.TaiKhoanDetails;
import com.fit.shoeshopbackend.dto.AuthRequest;
import com.fit.shoeshopbackend.dto.AuthResponse;
import com.fit.shoeshopbackend.dto.GoogleLoginRequest;
import com.fit.shoeshopbackend.dto.RegisterRequest;
import com.fit.shoeshopbackend.model.KhachHang;
import com.fit.shoeshopbackend.model.Role;
import com.fit.shoeshopbackend.model.TaiKhoan;
import com.fit.shoeshopbackend.repository.KhachHangRepository;
import com.fit.shoeshopbackend.repository.TaiKhoanRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private TaiKhoanRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    private KhachHangRepository khachHangRepository;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        var userDetails = (TaiKhoanDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);


        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new AuthResponse(token, userDetails.getUsername(), roles);
    }
    @Transactional
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest r) {
        if (userRepository.existsByTenDangNhap(r.getUsername())) return "Username exists";
        if (r.getEmail() != null && userRepository.existsByEmail(r.getEmail())) return "Email exists";

        TaiKhoan user = TaiKhoan.builder()
                .maTaiKhoan(UUID.randomUUID().toString())
                .tenDangNhap(r.getUsername())
                .email(r.getEmail())
                .matKhau(passwordEncoder.encode(r.getPassword()))
                .roles(Set.of(Role.ROLE_USER))
                .build();

        KhachHang kh = KhachHang.builder()
                .maKhachHang(UUID.randomUUID().toString())
                .hoTen(r.getFullName())
                .email(r.getEmail())
                .sdt(null)
                .diaChi(null)
                .diemTichLuy(0)
                .ngayThamGia(LocalDateTime.now())
                .tongChiTieu(0)
                .taiKhoan(user)
                .build();

        user.setKhachHang(kh);
        userRepository.save(user); // cascade ALL sẽ tự lưu KhachHang

        return "Registered";
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
            TaiKhoan user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // --- tạo tài khoản mới ---
                user = TaiKhoan.builder()
                        .maTaiKhoan(UUID.randomUUID().toString())
                        .tenDangNhap(email)
                        .email(email)
                        .matKhau(passwordEncoder.encode("GOOGLE_USER"))
                        .roles(Set.of(Role.ROLE_USER))
                        .build();

                KhachHang kh = KhachHang.builder()
                        .maKhachHang(UUID.randomUUID().toString())
                        .hoTen(fullName)
                        .email(email)
                        .diemTichLuy(0)
                        .ngayThamGia(LocalDateTime.now())
                        .tongChiTieu(0)
                        .taiKhoan(user)
                        .build();

                user.setKhachHang(kh);
                userRepository.save(user);
            }

            // --- tạo JWT ---
            TaiKhoanDetails details = new TaiKhoanDetails(user);
            String token = jwtUtil.generateToken(details);

            List<String> roles = user.getRoles().stream().map(Enum::name).toList();

            return new AuthResponse(token, user.getTenDangNhap(), roles);

        } catch (Exception e) {
            throw new RuntimeException("Login Google thất bại: " + e.getMessage());
        }
    }

}
