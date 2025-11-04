package com.skybooking.payment.dto.request;

import com.skybooking.payment.constants.PaymentConstants;
import com.skybooking.payment.constants.PaymentReferenceType;
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
public class CreateOrderRequest {
    @Size(max = PaymentConstants.MAX_REFERENCE_ID_LENGTH)
    private String referenceId;

    @NotNull(message = PaymentConstants.VALIDATION_REFERENCE_REQUIRED)
    private PaymentReferenceType referenceType;

    @NotNull(message = PaymentConstants.VALIDATION_AMOUNT_REQUIRED)
    @Positive(message = PaymentConstants.VALIDATION_AMOUNT_POSITIVE)
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @NotBlank(message = PaymentConstants.VALIDATION_CURRENCY_REQUIRED)
    @Pattern(regexp = "^[A-Z]{3}$", message = PaymentConstants.VALIDATION_CURRENCY_PATTERN)
    private String currency;

    @Size(max = PaymentConstants.MAX_DESCRIPTION_LENGTH, message = PaymentConstants.VALIDATION_DESCRIPTION_MAX)
    private String description;

    @Pattern(regexp = "^https?://.*", message = "Return URL must be a valid HTTP/HTTPS URL")
    private String returnUrl;

    @Pattern(regexp = "^https?://.*", message = "Cancel URL must be a valid HTTP/HTTPS URL")
    private String cancelUrl;
}
