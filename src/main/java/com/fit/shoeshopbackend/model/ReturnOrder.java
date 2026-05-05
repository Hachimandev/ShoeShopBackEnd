package com.fit.shoeshopbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnOrder {
    @Id
    private String returnOrderId;
    private LocalDateTime returnDate;
    private double refundAmount;

    @ManyToOne
    @JoinColumn(name = "customerId")
    @JsonIgnore
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "orderId")
    @JsonIgnore
    private Order order;

    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ReturnOrderDetail> returnOrderDetails;
}
