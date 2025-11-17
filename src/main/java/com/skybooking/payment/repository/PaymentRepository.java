package com.skybooking.payment.repository;

import com.skybooking.payment.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentModel, Long> {
    Optional<PaymentModel> findByOrderId(String orderId);
    Optional<PaymentModel> findByAuthorizationId(String authorizationId);
    Optional<PaymentModel> findByCaptureId(String captureId);
    Optional<PaymentModel> findByIdempotencyKey(String idempotencyKey);

    boolean existsByOrderId(String orderId);
    boolean existsByAuthorizationId(String authorizationId);
    boolean existsByCaptureId(String captureId);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
