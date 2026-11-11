package com.rupeek.maid.domain.strategy.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String gatewayResponse;
    private String failureReason;
}
