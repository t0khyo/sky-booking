package com.skybooking.payment.service;


import com.skybooking.payment.dto.request.AuthorizePaymentRequest;
import com.skybooking.payment.dto.request.CapturePaymentRequest;
import com.skybooking.payment.dto.request.CreateOrderRequest;
import com.skybooking.payment.dto.response.AuthorizationResponse;
import com.skybooking.payment.dto.response.CaptureResponse;
import com.skybooking.payment.dto.response.OrderResponse;

public interface PaymentService {


    OrderResponse createOrder(CreateOrderRequest request);

    AuthorizationResponse authorizePayment(AuthorizePaymentRequest request);

    CaptureResponse captureAuthorizedPayment(CapturePaymentRequest request);

    AuthorizationResponse voidAuthorizedPayment(String authorizationId);
}