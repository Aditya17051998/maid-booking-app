package com.rupeek.maid.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"booking"})
public class Payment {

    private Long id;
    private Booking booking;
    private String transactionId;
    private PaymentMethodType paymentMethod;
    private BigDecimal amount;
    private PaymentStatus status = PaymentStatus.PENDING;
    private String paymentGatewayResponse;
    private String failureReason;
    private String idempotencyKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void markAsCompleted(String gatewayResponse) {
        this.status = PaymentStatus.COMPLETED;
        this.paymentGatewayResponse = gatewayResponse;
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public void markAsRefunded() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public boolean canBeRefunded() {
        return this.status == PaymentStatus.COMPLETED;
    }
}
