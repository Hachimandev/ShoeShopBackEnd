package com.fit.shoeshopbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {
    @Id
    private String supplierId;
    private String supplierName;
    private String phoneNumber;
    private String email;
    private String address;

    @OneToMany(mappedBy = "supplier")
    @JsonIgnore
    private List<Product> products;
}
