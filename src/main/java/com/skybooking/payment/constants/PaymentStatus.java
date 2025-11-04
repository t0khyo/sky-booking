package com.skybooking.payment.constants;


public enum PaymentStatus {
    CREATED("Order created but not yet approved"),
    SAVED("Order saved but not yet approved"),
    APPROVED("Order approved by payer"),
    AUTHORIZED("Payment authorized"),
    CAPTURED("Payment captured"),
    VOIDED("Authorization voided"),
    FAILED("Payment failed");

    PaymentStatus(String description) {
    }
}