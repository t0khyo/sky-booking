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
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Received request to create order for amount: {} {}",
                request.getAmount(), request.getCurrency());
        //TODO check paypal payment method is active first
        OrderResponse response = payPalPaymentService.createOrder(request);

        log.info("Order created successfully with ID: {}", response.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @PostMapping("/paypal/authorize")
    public ResponseEntity<AuthorizationResponse> authorizePayment(
            @Valid @RequestBody AuthorizePaymentRequest request) {

        log.info("Received request to authorize payment for order: {}", request.getOrderId());
        //TODO check paypal payment method is active first
        AuthorizationResponse response = payPalPaymentService.authorizePayment(request);

        log.info("Payment authorized successfully with ID: {}", response.getAuthorizationId());
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/paypal/capturePayment")
    public ResponseEntity<CaptureResponse> capturePayment(
            @Valid @RequestBody CapturePaymentRequest request) {

        log.info("Received request to capture payment for authorization: {}", request.getAuthorizationId());
        //TODO check paypal payment method is active first
        CaptureResponse response = payPalPaymentService.captureAuthorizedPayment(request);

        log.info("Payment captured successfully with ID: {}", response.getCaptureId());
        return ResponseEntity.ok(response);
    }

   
    @PostMapping("/paypal/void/{authorizationId}")
    public ResponseEntity<AuthorizationResponse> voidPayment(@PathVariable String authorizationId) {
        log.info("Received request to void authorization: {}", authorizationId);
        //TODO check paypal payment method is active first
        AuthorizationResponse response = payPalPaymentService.voidAuthorizedPayment(authorizationId);

        log.info("Authorization voided successfully: {}", authorizationId);
        return ResponseEntity.ok(response);
    }
}