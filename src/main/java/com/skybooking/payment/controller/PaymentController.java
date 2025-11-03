package com.skybooking.payment.controller;

import com.skybooking.payment.dto.request.AuthorizePaymentRequest;
import com.skybooking.payment.dto.request.CapturePaymentRequest;
import com.skybooking.payment.dto.request.CreateOrderRequest;
import com.skybooking.payment.dto.response.AuthorizationResponse;
import com.skybooking.payment.dto.response.CaptureResponse;
import com.skybooking.payment.dto.response.OrderResponse;
import com.skybooking.payment.service.PaymentService;
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

    private final PaymentService paymentService;

    
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Received request to create order for amount: {} {}",
                request.getAmount(), request.getCurrency());

        OrderResponse response = paymentService.createOrder(request);

        log.info("Order created successfully with ID: {}", response.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationResponse> authorizePayment(
            @Valid @RequestBody AuthorizePaymentRequest request) {

        log.info("Received request to authorize payment for order: {}", request.getOrderId());

        AuthorizationResponse response = paymentService.authorizePayment(request);

        log.info("Payment authorized successfully with ID: {}", response.getAuthorizationId());
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/capturePayment")
    public ResponseEntity<CaptureResponse> capturePayment(
            @Valid @RequestBody CapturePaymentRequest request) {

        log.info("Received request to capture payment for authorization: {}", request.getAuthorizationId());

        CaptureResponse response = paymentService.captureAuthorizedPayment(request);

        log.info("Payment captured successfully with ID: {}", response.getCaptureId());
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/void/{authorizationId}")
    public ResponseEntity<AuthorizationResponse> voidPayment(@PathVariable String authorizationId) {
        log.info("Received request to void authorization: {}", authorizationId);

        AuthorizationResponse response = paymentService.voidAuthorizedPayment(authorizationId);

        log.info("Authorization voided successfully: {}", authorizationId);
        return ResponseEntity.ok(response);
    }
}