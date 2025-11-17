package com.skybooking.payment.service.paypal.impl;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import com.skybooking.payment.configs.PaypalConfigs;
import com.skybooking.payment.constants.PaymentConstants;
import com.skybooking.payment.dto.request.AuthorizePaymentRequest;
import com.skybooking.payment.dto.request.CapturePaymentRequest;
import com.skybooking.payment.dto.request.CreateOrderRequest;
import com.skybooking.payment.dto.response.AuthorizationResponse;
import com.skybooking.payment.dto.response.CaptureResponse;
import com.skybooking.payment.dto.response.LinkResponse;
import com.skybooking.payment.dto.response.OrderResponse;
import com.skybooking.payment.exception.PayPalException;
import com.skybooking.payment.exception.PaymentException;
import com.skybooking.payment.service.paypal.PayPalPaymentService;
import com.skybooking.payment.model.PaymentModel;
import com.skybooking.payment.constants.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.skybooking.payment.constants.PaymentConstants.DEFAULT_CURRENCY;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayPalPaymentServiceImpl implements PayPalPaymentService {

    private final PaypalServerSdkClient paypalClient;
    private final com.skybooking.payment.repository.PaymentRepository paymentRepository;

    private static final int ASYNC_TIMEOUT_SECONDS = 30;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey) {
        log.info("Creating PayPal order for amount: {} {}", request.getAmount(), request.getCurrency());

        try {
            // Idempotency fast-path
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                var existingOpt = paymentRepository.findByIdempotencyKey(idempotencyKey);
                if (existingOpt.isPresent() && existingOpt.get().getOrderId() != null) {
                    var existing = existingOpt.get();
                    log.info("Idempotency hit for createOrder; key={}", idempotencyKey);
                    return OrderResponse.builder()
                            .orderId(existing.getOrderId())
                            .status(existing.getStatus() != null ? existing.getStatus().name() : null)
                            .amount(existing.getAmount())
                            .currency(existing.getCurrency())
                            .createdAt(existing.getCreatedAt())
                            .build();
                }
            }

            OrdersController ordersController = paypalClient.getOrdersController();

            OrderRequest orderRequest = buildOrderRequest(request);

            CreateOrderInput input = new CreateOrderInput.Builder("application/json", orderRequest)
                    .prefer(PaymentConstants.PREFER_REPRESENTATION)
                    .build();

            CompletableFuture<ApiResponse<Order>> futureResponse =
                    ordersController.createOrderAsync(input);

            ApiResponse<Order> response = handleAsyncResponse(
                    futureResponse,
                    "create order",
                    ASYNC_TIMEOUT_SECONDS
            );

            Order order = response.getResult();
            log.info("PayPal order created successfully with ID: {}", order.getId());

            // Persist basic record (CREATED)
            PaymentModel entity = PaymentModel.builder()
                    .orderId(order.getId())
                    .status(com.skybooking.payment.constants.PaymentStatus.CREATED)
                    .amount(request.getAmount())
                    .currency(request.getCurrency() != null ? request.getCurrency() : DEFAULT_CURRENCY)
                    .idempotencyKey(idempotencyKey)
                    .build();
            paymentRepository.save(entity);

            return buildOrderResponse(order, request);

        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating order", e);
            throw new PaymentException(PaymentConstants.ERROR_ORDER_CREATION_FAILED, e);
        }
    }

    @Override
    @Transactional
    public AuthorizationResponse authorizePayment(AuthorizePaymentRequest request, String idempotencyKey) {
        log.info("Authorizing payment for order: {}", request.getOrderId());

        try {
            // Idempotency fast-path
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                var idem = paymentRepository.findByIdempotencyKey(idempotencyKey);
                if (idem.isPresent() && idem.get().getAuthorizationId() != null) {
                    log.info("Idempotency hit for authorize; key={}", idempotencyKey);
                    var entity = idem.get();
                    return AuthorizationResponse.builder()
                            .authorizationId(entity.getAuthorizationId())
                            .status(PaymentStatus.AUTHORIZED.name())
                            .createdAt(entity.getCreatedAt())
                            .payerEmail(entity.getPayerEmail())
                            .payerName(entity.getPayerName())
                            .build();
                }
            }

            // Verify transaction exists in the database
            var paymentOpt = paymentRepository.findByOrderId(request.getOrderId());
            if (paymentOpt.isEmpty()) {
                throw new PaymentException("Order not found: " + request.getOrderId());
            }

            OrdersController ordersController = paypalClient.getOrdersController();

            AuthorizeOrderInput input = new AuthorizeOrderInput.Builder(request.getOrderId(), "application/json")
                    .prefer(PaymentConstants.PREFER_REPRESENTATION)
                    .build();

            CompletableFuture<ApiResponse<OrderAuthorizeResponse>> futureResponse =
                    ordersController.authorizeOrderAsync(input);

            ApiResponse<OrderAuthorizeResponse> response = handleAsyncResponse(
                    futureResponse,
                    "authorize payment",
                    ASYNC_TIMEOUT_SECONDS
            );

            OrderAuthorizeResponse orderAuthorizeResponse = response.getResult();

            PurchaseUnit purchaseUnit = orderAuthorizeResponse.getPurchaseUnits().get(0);
            AuthorizationWithAdditionalData authorization =
                    purchaseUnit.getPayments().getAuthorizations().get(0);

            log.info("Payment authorized successfully with ID: {}", authorization.getId());

            // Persist AUTHORIZED state with payer info
            try {
                var paymentOpt2 = paymentRepository.findByOrderId(request.getOrderId());
                if (paymentOpt2.isEmpty()) {
                    throw new PaymentException("Order not found for authorization: " + request.getOrderId());
                }
                var payment = paymentOpt2.get();
                // idempotency short-circuit: if already authorized
                if (payment.getAuthorizationId() == null) {
                    payment.setAuthorizationId(authorization.getId());
                    payment.setStatus(PaymentStatus.AUTHORIZED);
                    if (orderAuthorizeResponse.getPayer() != null) {
                        Payer payer = orderAuthorizeResponse.getPayer();
                        payment.setPayerEmail(payer.getEmailAddress());
                        if (payer.getName() != null) {
                            payment.setPayerName(buildPayerName(payer.getName()));
                        }
                    }
                    // initialize remaining amount if not set
                    if (payment.getRemainingAuthorizedAmount() == null) {
                        payment.setRemainingAuthorizedAmount(payment.getAmount());
                    }
                    payment.setIdempotencyKey(idempotencyKey);
                    paymentRepository.save(payment);
                }
            } catch (Exception ex) {
                log.error("Failed to persist AUTHORIZED state for order {}", request.getOrderId(), ex);
                // optional: rethrow or continue returning response
            }

            return buildAuthorizationResponse(authorization, orderAuthorizeResponse.getPayer());

        } catch (PaymentException e) {
            // TODO: Update transaction with error if it exists
            log.error("Error authorizing payment for order: {}", request.getOrderId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while authorizing payment", e);
            throw new PaymentException(PaymentConstants.ERROR_AUTHORIZATION_FAILED, e);
        }
    }

    @Override
    @Transactional
    public CaptureResponse captureAuthorizedPayment(CapturePaymentRequest request, String idempotencyKey) {
        log.info("Capturing authorized payment: {}", request.getAuthorizationId());

        try {
            // Idempotency fast-path
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                var idem = paymentRepository.findByIdempotencyKey(idempotencyKey);
                if (idem.isPresent() && idem.get().getCaptureId() != null) {
                    log.info("Idempotency hit for capture; key={}", idempotencyKey);
                    var entity = idem.get();
                    return CaptureResponse.builder()
                            .captureId(entity.getCaptureId())
                            .authorizationId(entity.getAuthorizationId())
                            .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                            .amount(entity.getAmount())
                            .currency(entity.getCurrency())
                            .createdAt(entity.getCreatedAt())
                            .finalCapture(entity.getFinalCapture())
                            .build();
                }
            }

            // Verify authorization exists and is valid
            var paymentOpt = paymentRepository.findByAuthorizationId(request.getAuthorizationId());
            if (paymentOpt.isEmpty()) {
                throw new PaymentException("Authorization not found: " + request.getAuthorizationId());
            }
            var payment = paymentOpt.get();
            if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
                throw new PaymentException("Capture not allowed unless AUTHORIZED");
            }
            if (payment.getRemainingAuthorizedAmount() == null) {
                throw new PaymentException("No remaining authorized amount recorded");
            }
            if (request.getAmount().compareTo(payment.getRemainingAuthorizedAmount()) > 0) {
                throw new PaymentException("Capture amount exceeds remaining authorized amount");
            }
            if (request.getCurrency() != null && !request.getCurrency().equalsIgnoreCase(payment.getCurrency())) {
                throw new PaymentException("Capture currency must match authorization currency");
            }

            PaymentsController paymentsController = paypalClient.getPaymentsController();

            CaptureRequest captureRequest = buildCaptureRequestBody(request);

            CaptureAuthorizedPaymentInput input = new CaptureAuthorizedPaymentInput.Builder()
                    .authorizationId(request.getAuthorizationId())
                    .prefer(PaymentConstants.PREFER_REPRESENTATION)
                    .body(captureRequest)
                    .build();

            CompletableFuture<ApiResponse<CapturedPayment>> futureResponse =
                    paymentsController.captureAuthorizedPaymentAsync(input);

            ApiResponse<CapturedPayment> response = handleAsyncResponse(
                    futureResponse,
                    "capture payment",
                    ASYNC_TIMEOUT_SECONDS
            );

            CapturedPayment capturedPayment = response.getResult();
            log.info("Payment captured successfully with ID: {}", capturedPayment.getId());

            // Persist CAPTURED state
            try {
                var currentOpt = paymentRepository.findByAuthorizationId(request.getAuthorizationId());
                if (currentOpt.isEmpty()) {
                    throw new PaymentException("Authorization missing during update");
                }
                var current = currentOpt.get();
                if (current.getCaptureId() == null) {
                    // Determine captured amount from PayPal result if available
                    BigDecimal capturedAmount = request.getAmount();
                    if (capturedPayment.getAmount() != null && capturedPayment.getAmount().getValue() != null) {
                        try { capturedAmount = new BigDecimal(capturedPayment.getAmount().getValue()); } catch (NumberFormatException ignore) {}
                    }
                    BigDecimal newRemaining = current.getRemainingAuthorizedAmount().subtract(capturedAmount);
                    current.setRemainingAuthorizedAmount(newRemaining);
                    current.setCaptureId(capturedPayment.getId());
                    boolean isFinal = Boolean.TRUE.equals(request.getFinalCapture()) || newRemaining.compareTo(BigDecimal.ZERO) <= 0;
                    current.setFinalCapture(isFinal);
                    if (isFinal) {
                        current.setStatus(PaymentStatus.CAPTURED);
                    }
                    current.setIdempotencyKey(idempotencyKey);
                    paymentRepository.save(current);
                }
            } catch (Exception ex) {
                log.error("Failed to persist CAPTURED state for authorization {}", request.getAuthorizationId(), ex);
            }

            return buildCaptureResponseFromCapturedPayment(
                    capturedPayment,
                    request
            );

        } catch (PaymentException e) {
            // TODO: Update transaction with error if it exists
            log.error("Error capturing payment for authorization: {}", request.getAuthorizationId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while capturing payment", e);
            throw new PaymentException(PaymentConstants.ERROR_CAPTURE_FAILED, e);
        }
    }

    @Override
    @Transactional
    public AuthorizationResponse voidAuthorizedPayment(String authorizationId, String idempotencyKey) {
        log.info("Voiding authorized payment: {}", authorizationId);

        try {
            // Idempotency fast-path
            if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                var idem = paymentRepository.findByIdempotencyKey(idempotencyKey);
                if (idem.isPresent() && idem.get().getStatus() == PaymentStatus.VOIDED) {
                    log.info("Idempotency hit for void; key={}", idempotencyKey);
                    var entity = idem.get();
                    return AuthorizationResponse.builder()
                            .authorizationId(entity.getAuthorizationId())
                            .status(PaymentStatus.VOIDED.name())
                            .createdAt(LocalDateTime.now())
                            .payerEmail(entity.getPayerEmail())
                            .payerName(entity.getPayerName())
                            .build();
                }
            }

            // Verify current state
            var paymentOpt = paymentRepository.findByAuthorizationId(authorizationId);
            if (paymentOpt.isEmpty()) {
                throw new PaymentException("Authorization not found: " + authorizationId);
            }
            var payment = paymentOpt.get();
            if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
                throw new PaymentException("Only AUTHORIZED payments can be voided");
            }
            if (payment.getCaptureId() != null) {
                throw new PaymentException("Cannot void an authorization that has been captured");
            }

            PaymentsController paymentsController = paypalClient.getPaymentsController();

            VoidPaymentInput input = new VoidPaymentInput.Builder(authorizationId)
                    .prefer(PaymentConstants.PREFER_REPRESENTATION)
                    .build();

            CompletableFuture<ApiResponse<PaymentAuthorization>> futureResponse =
                    paymentsController.voidPaymentAsync(input);

            ApiResponse<PaymentAuthorization> response = handleAsyncResponse(
                    futureResponse,
                    "void payment",
                    ASYNC_TIMEOUT_SECONDS
            );

            log.info("Authorization voided successfully: {}", authorizationId);

            // Persist VOIDED state
            try {
                var currentOpt = paymentRepository.findByAuthorizationId(authorizationId);
                if (currentOpt.isEmpty()) {
                    throw new PaymentException("Authorization missing during update");
                }
                var current = currentOpt.get();
                if (current.getStatus() != PaymentStatus.VOIDED) {
                    current.setStatus(PaymentStatus.VOIDED);
                    current.setIdempotencyKey(idempotencyKey);
                    paymentRepository.save(current);
                }
            } catch (Exception ex) {
                log.error("Failed to persist VOIDED state for authorization {}", authorizationId, ex);
            }

            return buildAuthorizationResponseFromPaymentAuthorization(
                    response.getResult()
            );

        } catch (PaymentException e) {
            // TODO: Update transaction with error if it exists
            log.error("Error voiding authorization: {}", authorizationId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while voiding authorization", e);
            throw new PaymentException(PaymentConstants.ERROR_VOID_FAILED, e);
        }
    }


    private <T> T handleAsyncResponse(
            CompletableFuture<T> futureResponse,
            String operationName,
            int timeoutSeconds) {

        try {
            T response = futureResponse.get(timeoutSeconds, TimeUnit.SECONDS);
            log.debug("Async {} operation completed successfully", operationName);
            return response;

        } catch (TimeoutException e) {
            String errorMsg = String.format("Timeout while waiting for %s response", operationName);
            log.error(errorMsg, e);
            throw new PaymentException(errorMsg, e);

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            if (cause instanceof ApiException) {
                ApiException apiException = (ApiException) cause;
                String errorMsg = String.format(
                        "PayPal API error during %s: %s",
                        operationName,
                        apiException.getMessage()
                );
                log.error("{} - Status code: {}", errorMsg, apiException.getResponseCode(), apiException);
                throw new PayPalException(
                        errorMsg,
                        String.valueOf(apiException.getResponseCode()),
                        apiException
                );
            } else {
                String errorMsg = String.format("Execution error during %s", operationName);
                log.error(errorMsg, cause);
                throw new PaymentException(errorMsg, cause);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMsg = String.format("Thread interrupted during %s", operationName);
            log.error(errorMsg, e);
            throw new PaymentException(errorMsg, e);
        }
    }


    private OrderRequest buildOrderRequest(CreateOrderRequest request) {
        AmountWithBreakdown amount = new AmountWithBreakdown.Builder(
                request.getCurrency(),
                request.getAmount().toString()
        ).build();

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest.Builder(amount)
                .referenceId(request.getReferenceId())
                .description(request.getDescription())
                .build();

        OrderApplicationContext applicationContext = null;
        if (request.getReturnUrl() != null || request.getCancelUrl() != null) {
            applicationContext = new OrderApplicationContext.Builder()
                    .returnUrl(request.getReturnUrl())
                    .cancelUrl(request.getCancelUrl())
                    .build();
        }

        OrderRequest.Builder orderRequestBuilder = new OrderRequest.Builder(
                CheckoutPaymentIntent.AUTHORIZE,
                Collections.singletonList(purchaseUnit)
        );

        if (applicationContext != null) {
            orderRequestBuilder.applicationContext(applicationContext);
        }

        return orderRequestBuilder.build();
    }

    private CaptureRequest buildCaptureRequestBody(CapturePaymentRequest request) {
        Money amount = new Money.Builder(
                request.getCurrency() != null ? request.getCurrency() : DEFAULT_CURRENCY,
                request.getAmount().toString()
        ).build();

        CaptureRequest.Builder captureBuilder = new CaptureRequest.Builder()
                .amount(amount)
                .finalCapture(request.getFinalCapture() != null ? request.getFinalCapture() : true);

        if (request.getInvoiceId() != null) {
            captureBuilder.invoiceId(request.getInvoiceId());
        }

        if (request.getNoteToPayer() != null) {
            captureBuilder.noteToPayer(request.getNoteToPayer());
        }

        return captureBuilder.build();
    }

    private String buildPayerName(Name name) {
        if (name == null) {
            return null;
        }

        StringBuilder nameBuilder = new StringBuilder();
        if (name.getGivenName() != null) {
            nameBuilder.append(name.getGivenName());
        }
        if (name.getSurname() != null) {
            if (nameBuilder.length() > 0) {
                nameBuilder.append(" ");
            }
            nameBuilder.append(name.getSurname());
        }
        return nameBuilder.toString().trim();
    }


    private OrderResponse buildOrderResponse(Order order, CreateOrderRequest request) {
        List<LinkResponse> links = new ArrayList<>();

        if (order.getLinks() != null) {
            links = order.getLinks().stream()
                    .map(link -> LinkResponse.builder()
                            .href(link.getHref())
                            .rel(link.getRel())
                            .method(link.getMethod() != null ? link.getMethod().toString() : null)
                            .build())
                    .collect(Collectors.toList());
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().toString())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .links(links)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private AuthorizationResponse buildAuthorizationResponse(
            AuthorizationWithAdditionalData authorization,
            Payer payer) {

        LocalDateTime expiresAt = null;
        if (authorization.getExpirationTime() != null) {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(authorization.getExpirationTime());
            expiresAt = zonedDateTime.toLocalDateTime();
        }

        String payerEmail = null;
        String payerName = null;

        if (payer != null) {
            payerEmail = payer.getEmailAddress();
            payerName = buildPayerName(payer.getName());
        }

        return AuthorizationResponse.builder()
                .authorizationId(authorization.getId())
                .status(authorization.getStatus().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .payerEmail(payerEmail)
                .payerName(payerName)
                .build();
    }

    private AuthorizationResponse buildAuthorizationResponseFromPaymentAuthorization(
            PaymentAuthorization authorization) {

        LocalDateTime expiresAt = null;
        if (authorization.getExpirationTime() != null) {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(authorization.getExpirationTime());
            expiresAt = zonedDateTime.toLocalDateTime();
        }

        return AuthorizationResponse.builder()
                .authorizationId(authorization.getId())
                .status(authorization.getStatus().toString())
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();
    }

    private CaptureResponse buildCaptureResponseFromCapturedPayment(
            CapturedPayment capturedPayment,
            CapturePaymentRequest request) {

        return CaptureResponse.builder()
                .captureId(capturedPayment.getId())
                .authorizationId(request.getAuthorizationId())
                .status(capturedPayment.getStatus().toString())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .createdAt(LocalDateTime.now())
                .finalCapture(request.getFinalCapture() != null ? request.getFinalCapture() : true)
                .build();
    }
}