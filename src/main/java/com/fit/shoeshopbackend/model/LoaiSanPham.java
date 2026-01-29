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
public class LoaiSanPham {
    @Id
    private String maLoai;
    private String tenLoai;

    @OneToMany(mappedBy = "loaiSanPham", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SanPham> sanPhams;
}
