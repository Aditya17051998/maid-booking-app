package com.rupeek.maid.domain.strategy.payment;

import com.rupeek.maid.domain.model.Payment;
import com.rupeek.maid.domain.model.PaymentMethodType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
public class CardPaymentMethod implements PaymentMethod {

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.CARD;
    }

    @Override
    public PaymentResult processPayment(Payment payment) {
        log.info("Processing card payment for transaction: {}", payment.getTransactionId());

        try {
            String gatewayTxnId = "CARD-" + UUID.randomUUID().toString();
            String response = String.format("Card payment successful. Gateway Transaction ID: %s", gatewayTxnId);

            return PaymentResult.builder()
                .success(true)
                .transactionId(gatewayTxnId)
                .gatewayResponse(response)
                .build();
        } catch (Exception e) {
            log.error("Card payment failed", e);
            return PaymentResult.builder()
                .success(false)
                .failureReason(e.getMessage())
                .build();
        }
    }

    @Override
    public PaymentResult refund(Payment payment) {
        log.info("Processing card refund for transaction: {}", payment.getTransactionId());

        try {
            String refundTxnId = "REFUND-" + UUID.randomUUID().toString();
            String response = String.format("Card refund successful. Refund Transaction ID: %s", refundTxnId);

            return PaymentResult.builder()
                .success(true)
                .transactionId(refundTxnId)
                .gatewayResponse(response)
                .build();
        } catch (Exception e) {
            log.error("Card refund failed", e);
            return PaymentResult.builder()
                .success(false)
                .failureReason(e.getMessage())
                .build();
        }
    }
}
