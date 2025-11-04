package com.skybooking.payment.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String approvalUrl;
    private LocalDateTime createdAt;
}
