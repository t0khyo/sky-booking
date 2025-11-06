package com.skybooking.payment.dto.request;


import com.skybooking.payment.constants.PaymentConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapturePaymentRequest {
    @NotNull(message = PaymentConstants.VALIDATION_AUTHORIZATION_ID_REQUIRED)
    @NotBlank(message = PaymentConstants.VALIDATION_AUTHORIZATION_ID_REQUIRED)
    String authorizationId;

    @NotNull(message = "Capture amount is required")
    @Positive(message = "Capture amount must be positive")
    private BigDecimal amount;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code")
    private String currency;

    private String invoiceId;

    @Size(max = 255, message = "Note to payer cannot exceed 255 characters")
    private String noteToPayer;

    private Boolean finalCapture;
}
