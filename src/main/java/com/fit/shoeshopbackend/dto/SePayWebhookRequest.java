package com.fit.shoeshopbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SePayWebhookRequest {
    private Long id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String code;
    private String content;
    private String transferType; // "in" for incoming, "out" for outgoing
    private Double transferAmount;
    private Double accumulated;
    private String subAccount;
    private String referenceCode;
}
