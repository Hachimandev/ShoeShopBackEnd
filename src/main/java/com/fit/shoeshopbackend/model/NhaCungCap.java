package com.fit.shoeshopbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhaCungCap {
    @Id
    private String maNhaCungCap;
    private String tenNhaCungCap;
    private String sdt;
    private String email;
    private String diaChi;

    @OneToMany(mappedBy = "nhaCungCap")
    @JsonIgnore
    private List<SanPham> sanPhams;
}
