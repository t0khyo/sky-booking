package com.skybooking.payment.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    //Todo add paymentId
    private String orderId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String description;
    private List<LinkResponse> links;
    private LocalDateTime createdAt;
}
