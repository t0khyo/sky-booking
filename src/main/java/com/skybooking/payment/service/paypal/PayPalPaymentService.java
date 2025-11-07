package com.skybooking.payment.service.paypal;


import com.skybooking.payment.dto.request.AuthorizePaymentRequest;
import com.skybooking.payment.dto.request.CapturePaymentRequest;
import com.skybooking.payment.dto.request.CreateOrderRequest;
import com.skybooking.payment.dto.response.AuthorizationResponse;
import com.skybooking.payment.dto.response.CaptureResponse;
import com.skybooking.payment.dto.response.OrderResponse;

public interface PayPalPaymentService {


    OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey);

    AuthorizationResponse authorizePayment(AuthorizePaymentRequest request, String idempotencyKey);

    CaptureResponse captureAuthorizedPayment(CapturePaymentRequest request, String idempotencyKey);

    AuthorizationResponse voidAuthorizedPayment(String authorizationId, String idempotencyKey);
}