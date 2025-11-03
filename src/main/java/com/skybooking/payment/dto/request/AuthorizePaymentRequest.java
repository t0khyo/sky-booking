package com.skybooking.payment.dto.request;


import com.skybooking.payment.constants.PaymentConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizePaymentRequest {

    @NotBlank(message = PaymentConstants.VALIDATION_ORDER_ID_REQUIRED)
    private String orderId;
}
