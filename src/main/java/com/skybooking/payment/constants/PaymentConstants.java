package com.skybooking.payment.constants;


public final class PaymentConstants {

    // PayPal Constants
    public static final String PREFER_MINIMAL = "return=minimal";
    public static final String PREFER_REPRESENTATION = "return=representation";

    // Error Messages
    public static final String ERROR_ORDER_CREATION_FAILED = "Failed to create PayPal order";
    public static final String ERROR_AUTHORIZATION_FAILED = "Failed to authorize payment";
    public static final String ERROR_CAPTURE_FAILED = "Failed to capture payment";
    public static final String ERROR_VOID_FAILED = "Failed to void authorization";
    public static final String ERROR_PAYPAL_API = "PayPal API error occurred";
    public static final String ERROR_ASYNC_EXECUTION = "Async execution error";
    public static final String ERROR_THREAD_INTERRUPTED = "Thread interrupted during PayPal operation";


    // Validation Messages
    public static final String VALIDATION_AMOUNT_REQUIRED = "Amount is required";
    public static final String VALIDATION_AMOUNT_POSITIVE = "Amount must be positive";
    public static final String VALIDATION_CURRENCY_REQUIRED = "Currency is required";
    public static final String VALIDATION_CURRENCY_PATTERN = "Currency must be a valid 3-letter ISO code";
    public static final String VALIDATION_ORDER_ID_REQUIRED = "Order ID is required";
    public static final String VALIDATION_AUTHORIZATION_ID_REQUIRED = "Authorization ID is required";
    public static final String VALIDATION_DESCRIPTION_MAX = "Description cannot exceed 127 characters";

    // Transaction Limits
    public static final int MAX_DESCRIPTION_LENGTH = 127;
    public static final int MAX_REFERENCE_ID_LENGTH = 256;

    //Default
    public static final String DEFAULT_CURRENCY = "USD";


}