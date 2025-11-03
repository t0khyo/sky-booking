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
public class CaptureResponse {
    //Todo add paymentId
    private String captureId;
    private String authorizationId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime createdAt;
    private Boolean finalCapture;
}