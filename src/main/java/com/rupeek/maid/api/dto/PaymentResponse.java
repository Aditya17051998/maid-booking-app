package com.rupeek.maid.api.dto;

import com.rupeek.maid.domain.model.PaymentMethodType;
import com.rupeek.maid.domain.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private String transactionId;
    private Long bookingId;
    private PaymentMethodType paymentMethod;
    private BigDecimal amount;
    private PaymentStatus status;
    private String gatewayResponse;
    private String failureReason;
    private LocalDateTime createdAt;
}
