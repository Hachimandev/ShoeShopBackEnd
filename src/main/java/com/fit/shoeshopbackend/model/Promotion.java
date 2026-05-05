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
public class Promotion {
    @Id
    private String promotionId;
    private Date startDate;
    private Date endDate;
    
    @Column(name = "promo_condition")
    private String condition;
    
    private double discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staffId")
    @JsonIgnore
    private Staff staff;

    @OneToMany(mappedBy = "promotion")
    @JsonIgnore
    private List<Order> orders;
}
