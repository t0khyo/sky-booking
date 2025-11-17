package com.skybooking.payment.controller;

import com.skybooking.payment.dto.request.AuthorizePaymentRequest;
import com.skybooking.payment.dto.request.CapturePaymentRequest;
import com.skybooking.payment.dto.request.CreateOrderRequest;
import com.skybooking.payment.dto.response.AuthorizationResponse;
import com.skybooking.payment.dto.response.CaptureResponse;
import com.skybooking.payment.dto.response.OrderResponse;
import com.skybooking.payment.service.paypal.PayPalPaymentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PayPalPaymentService payPalPaymentService;

    
    @PostMapping("/paypal/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("Received request to create order for amount: {} {}",
                request.getAmount(), request.getCurrency());

        OrderResponse response = payPalPaymentService.createOrder(request, idempotencyKey);

        log.info("Order created successfully with ID: {}", response.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @PostMapping("/paypal/authorize")
    public ResponseEntity<AuthorizationResponse> authorizePayment(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody AuthorizePaymentRequest request) {

        log.info("Received request to authorize payment for order: {}", request.getOrderId());

        AuthorizationResponse response = payPalPaymentService.authorizePayment(request, idempotencyKey);

        log.info("Payment authorized successfully with ID: {}", response.getAuthorizationId());
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/paypal/capturePayment")
    public ResponseEntity<CaptureResponse> capturePayment(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CapturePaymentRequest request) {

        log.info("Received request to capture payment for authorization: {}", request.getAuthorizationId());

        CaptureResponse response = payPalPaymentService.captureAuthorizedPayment(request, idempotencyKey);

        log.info("Payment captured successfully with ID: {}", response.getCaptureId());
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/paypal/void/{authorizationId}")
    public ResponseEntity<AuthorizationResponse> voidPayment(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @PathVariable String authorizationId) {
        log.info("Received request to void authorization: {}", authorizationId);

        AuthorizationResponse response = payPalPaymentService.voidAuthorizedPayment(authorizationId, idempotencyKey);

        log.info("Authorization voided successfully: {}", authorizationId);
        return ResponseEntity.ok(response);
    }
}