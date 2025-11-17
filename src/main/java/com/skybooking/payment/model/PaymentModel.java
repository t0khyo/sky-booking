package com.skybooking.payment.model;

import com.skybooking.payment.constants.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "payments",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_payments_order_id", columnNames = {"order_id"}),
        @UniqueConstraint(name = "uk_payments_authorization_id", columnNames = {"authorization_id"}),
        @UniqueConstraint(name = "uk_payments_capture_id", columnNames = {"capture_id"}),
        @UniqueConstraint(name = "uk_payments_idempotency_key", columnNames = {"idempotency_key"})
    },
    indexes = {
        @Index(name = "idx_payments_status", columnList = "status"),
        @Index(name = "idx_payments_created_at", columnList = "created_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // External IDs from PayPal
    @Column(name = "order_id", length = 64, unique = true)
    private String orderId;

    @Column(name = "authorization_id", length = 64, unique = true)
    private String authorizationId;

    @Column(name = "capture_id", length = 64, unique = true)
    private String captureId;

    // Lifecycle status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private PaymentStatus status;

    // Monetary fields
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "remaining_authorized_amount", precision = 19, scale = 2)
    private BigDecimal remainingAuthorizedAmount;

    @Column(name = "final_capture")
    private Boolean finalCapture;

    // Payer details (from PayPal payer object)
    @Column(name = "payer_email", length = 255)
    private String payerEmail;

    @Column(name = "payer_name", length = 255)
    private String payerName;

    // Idempotency key used for de-duplication of client requests
    @Column(name = "idempotency_key", length = 128, unique = true)
    private String idempotencyKey;

    // Auditing
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime  createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Optimistic locking for concurrency protection
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
