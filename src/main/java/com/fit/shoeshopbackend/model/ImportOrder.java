package com.fit.shoeshopbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportOrder {
    @Id
    private String importOrderId;
    private Date importDate;

    @ManyToOne
    @JoinColumn(name = "staffId")
    @JsonIgnore
    private Staff staff;

    @OneToMany(mappedBy = "importOrder", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ImportOrderDetail> importOrderDetails;
}
