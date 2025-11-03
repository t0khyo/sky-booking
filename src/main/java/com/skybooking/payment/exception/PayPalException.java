package com.skybooking.payment.exception;

import lombok.Getter;

@Getter
public class PayPalException extends PaymentException {

    private final String paypalErrorCode;

    public PayPalException(String message, String paypalErrorCode) {
        super(message);
        this.paypalErrorCode = paypalErrorCode;
    }

    public PayPalException(String message, String paypalErrorCode, Throwable cause) {
        super(message, cause);
        this.paypalErrorCode = paypalErrorCode;
    }
}