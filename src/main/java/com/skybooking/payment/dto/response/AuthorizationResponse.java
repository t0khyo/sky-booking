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
public class AuthorizationResponse {
    private String authorizationId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String payerEmail;
    private String payerName;
}
