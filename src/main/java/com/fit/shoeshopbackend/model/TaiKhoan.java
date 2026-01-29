package com.fit.shoeshopbackend.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaiKhoan {

    @Id
    private String maTaiKhoan;

    @Column(nullable = false, unique = true)
    private String tenDangNhap;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private String matKhau;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "taiKhoan_role", joinColumns = @JoinColumn(name = "maTaiKhoan"))
    @Column(name = "role")
    private Set<Role> roles;

    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL)
    private KhachHang khachHang;

    @Override
    public String toString() {
        return tenDangNhap + " - " + roles;
    }
}


