package com.fit.shoeshopbackend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PromotionRequest {
    private String promotionId;
    private Date startDate;
    private Date endDate;
    private String condition;
    private double discount;
    private String staffId;
}









