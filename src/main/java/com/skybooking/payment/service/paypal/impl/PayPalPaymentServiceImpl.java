package com.skybooking.payment.service.paypal.impl;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import com.skybooking.payment.constants.PaymentConstants;
import com.skybooking.payment.dto.request.AuthorizePaymentRequest;
import com.skybooking.payment.dto.request.CapturePaymentRequest;
import com.skybooking.payment.dto.request.CreateOrderRequest;
import com.skybooking.payment.dto.response.AuthorizationResponse;
import com.skybooking.payment.dto.response.CaptureResponse;
import com.skybooking.payment.dto.response.OrderResponse;
import com.skybooking.payment.exception.PaymentException;
import com.skybooking.payment.service.paypal.PayPalPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.skybooking.payment.constants.PaymentConstants.DEFAULT_CURRENCY;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayPalPaymentServiceImpl implements PayPalPaymentService {

    private final PaypalServerSdkClient paypalClient;

    private static final int ASYNC_TIMEOUT_SECONDS = 30;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating PayPal order for amount: {} {}", request.getAmount(), request.getCurrency());

        try {
            OrdersController ordersController = paypalClient.getOrdersController();

            OrderRequest orderRequest = buildOrderRequest(request);

            CreateOrderInput input = new CreateOrderInput.Builder("application/json", orderRequest)
                    .prefer(PaymentConstants.PREFER_REPRESENTATION)
                    .build();

            CompletableFuture<ApiResponse<Order>> futureResponse =
                    ordersController.createOrderAsync(input);

            ApiResponse<Order> response = handleAsyncResponse(
                    futureResponse,
                    "create order"
            );

            Order order = response.getResult();
            log.info("PayPal order created successfully with ID: {}", order.getId());

            //TODO create payment in database with status CREATED
            //TODO Notify booking module to add paymentId to booking record

            return buildOrderResponse(order, request);

        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            //TODO create payment in database with status FAILED

            log.error("Unexpected error while creating order", e);
            throw new PaymentException(PaymentConstants.ERROR_ORDER_CREATION_FAILED, e);
        }
    }

    @Override
    @Transactional
    public AuthorizationResponse authorizePayment(AuthorizePaymentRequest request) {
        log.info("Authorizing payment for order: {}", request.getOrderId());

        try {
            //TODO Verify payment exists in the database with orderId
            //TODO update payment status to Approved

            OrdersController ordersController = paypalClient.getOrdersController();

            AuthorizeOrderInput input = new AuthorizeOrderInput.Builder(request.getOrderId(), "application/json")
                    .prefer(PaymentConstants.PREFER_REPRESENTATION)
                    .build();

            CompletableFuture<ApiResponse<OrderAuthorizeResponse>> futureResponse =
                    ordersController.authorizeOrderAsync(input);

            ApiResponse<OrderAuthorizeResponse> response = handleAsyncResponse(
                    futureResponse,
                    "authorize payment"
            );

            OrderAuthorizeResponse orderAuthorizeResponse = response.getResult();

            AuthorizationWithAdditionalData authorization = getAuthorization(orderAuthorizeResponse);

            log.info("Payment authorized successfully with ID: {}", authorization.getId());

            //TODO update payment status to Authorized
            //TODO create transaction in DB with type AUTHORIZE and response status

            return buildAuthorizationResponse(authorization, orderAuthorizeResponse.getPayer());

        } catch (PaymentException e) {
            //TODO create transaction in DB with type AUTHORIZE and status Failed
            log.error("Error authorizing payment for order: {}", request.getOrderId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while authorizing payment", e);
            throw new PaymentException(PaymentConstants.ERROR_AUTHORIZATION_FAILED, e);
        }
    }

    private static AuthorizationWithAdditionalData getAuthorization(OrderAuthorizeResponse orderAuthorizeResponse) {
        PurchaseUnit purchaseUnit = orderAuthorizeResponse.getPurchaseUnits().get(0);
        AuthorizationWithAdditionalData authorizationWithAdditionalData = purchaseUnit.getPayments().getAuthorizations().get(0);
        return authorizationWithAdditionalData;
    }

    @Override
    @Transactional
    public CaptureResponse captureAuthorizedPayment(CapturePaymentRequest request) {
        log.info("Capturing authorized payment: {}", request.getAuthorizationId());

        try {
            //TODO Verify payment exists in the database and authorized

            PaymentsController paymentsController = paypalClient.getPaymentsController();

            CaptureRequest captureRequest = buildCaptureRequestBody(request);

            CaptureAuthorizedPaymentInput input = getCaptureAuthorizedPaymentInput(request, captureRequest);

            CompletableFuture<ApiResponse<CapturedPayment>> futureResponse =
                    paymentsController.captureAuthorizedPaymentAsync(input);

            ApiResponse<CapturedPayment> response = handleAsyncResponse(
                    futureResponse,
                    "capture payment"
            );

            CapturedPayment capturedPayment = response.getResult();
            log.info("Payment captured successfully with ID: {}", capturedPayment.getId());

            //TODO Update payment with status CAPTURED
            //TODO create transaction in DB with type Capture and response status


            return buildCaptureResponseFromCapturedPayment(
                    capturedPayment,
                    request
            );

        } catch (PaymentException e) {
            // TODO: Update transaction with status Failed
            log.error("Error capturing payment for authorization: {}", request.getAuthorizationId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while capturing payment", e);
            throw new PaymentException(PaymentConstants.ERROR_CAPTURE_FAILED, e);
        }
    }

    private static CaptureAuthorizedPaymentInput getCaptureAuthorizedPaymentInput(CapturePaymentRequest request, CaptureRequest captureRequest) {
        return new CaptureAuthorizedPaymentInput.Builder()
                .authorizationId(request.getAuthorizationId())
                .prefer(PaymentConstants.PREFER_REPRESENTATION)
                .body(captureRequest)
                .build();
    }

    @Override
    @Transactional
    public AuthorizationResponse voidAuthorizedPayment(String authorizationId) {
        log.info("Voiding authorized payment: {}", authorizationId);

        try {
            //TODO Verify payment exists in the database and authorized

            PaymentsController paymentsController = paypalClient.getPaymentsController();

            VoidPaymentInput input = new VoidPaymentInput.Builder(authorizationId)
                    .prefer(PaymentConstants.PREFER_REPRESENTATION)
                    .build();

            CompletableFuture<ApiResponse<PaymentAuthorization>> futureResponse =
                    paymentsController.voidPaymentAsync(input);

            ApiResponse<PaymentAuthorization> response = handleAsyncResponse(
                    futureResponse,
                    "void payment"
            );

            log.info("Authorization voided successfully: {}", authorizationId);

            //TODO Update payment with status Voided
            //TODO create transaction in DB with type Void and response status


            return buildAuthorizationResponseFromPaymentAuthorization(
                    response.getResult()
            );

        } catch (PaymentException e) {
            // TODO: Update transaction with status Failed
            log.error("Error voiding authorization: {}", authorizationId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while voiding authorization", e);
            throw new PaymentException(PaymentConstants.ERROR_VOID_FAILED, e);
        }
    }


    private <T> T handleAsyncResponse(
            CompletableFuture<T> futureResponse,
            String operationName) {

        try {
            T response = futureResponse.get(PayPalPaymentServiceImpl.ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.debug("Async {} operation completed successfully", operationName);
            return response;

        } catch (TimeoutException e) {
            String errorMsg = String.format("Timeout while waiting for %s response", operationName);
            log.error(errorMsg, e);
            throw new PaymentException(errorMsg, e);

        } catch (Exception e) {
            String errorMsg = String.format("Exception during %s", operationName);
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

        OrderApplicationContext applicationContext = null;  //TODO replace OrderApplicationContext with experience_context
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
        String approvalUrl = "";

        if (order.getLinks() != null) {
            approvalUrl = getApprovalUrl(order);
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().toString())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .approvalUrl(approvalUrl)
                .createdAt(getLocalDateTime(order.getCreateTime()))
                .build();
    }

    private static LocalDateTime getLocalDateTime(String dateTime) {
        return ZonedDateTime.parse(dateTime).toLocalDateTime();
    }


    private static String getApprovalUrl(Order order) {
        return order.getLinks().stream()
                .filter(link -> "approve".equalsIgnoreCase(link.getRel()))
                .map(LinkDescription::getHref)
                .findFirst()
                .orElse("");
    }

    private AuthorizationResponse buildAuthorizationResponse(
            AuthorizationWithAdditionalData authorization,
            Payer payer) {

        LocalDateTime expiresAt = null;
        if (authorization != null) {
            expiresAt = getLocalDateTime(authorization.getExpirationTime());
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
                .createdAt(getLocalDateTime(authorization.getCreateTime()))
                .expiresAt(expiresAt)
                .payerEmail(payerEmail)
                .payerName(payerName)
                .build();
    }

    private AuthorizationResponse buildAuthorizationResponseFromPaymentAuthorization(
            PaymentAuthorization authorization) {

        LocalDateTime expiresAt = getLocalDateTime(authorization.getExpirationTime());

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