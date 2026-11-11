package com.rupeek.maid.domain.strategy.payment;

import com.rupeek.maid.domain.model.Payment;
import com.rupeek.maid.domain.model.PaymentMethodType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
public class WalletPaymentMethod implements PaymentMethod {

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.WALLET;
    }

    @Override
    public PaymentResult processPayment(Payment payment) {
        log.info("Processing Wallet payment for transaction: {}", payment.getTransactionId());

        try {
            String gatewayTxnId = "WALLET-" + UUID.randomUUID().toString();
            String response = String.format("Wallet payment successful. Gateway Transaction ID: %s", gatewayTxnId);

            return PaymentResult.builder()
                .success(true)
                .transactionId(gatewayTxnId)
                .gatewayResponse(response)
                .build();
        } catch (Exception e) {
            log.error("Wallet payment failed", e);
            return PaymentResult.builder()
                .success(false)
                .failureReason(e.getMessage())
                .build();
        }
    }

    @Override
    public PaymentResult refund(Payment payment) {
        log.info("Processing Wallet refund for transaction: {}", payment.getTransactionId());

        try {
            String refundTxnId = "REFUND-" + UUID.randomUUID().toString();
            String response = String.format("Wallet refund successful. Refund Transaction ID: %s", refundTxnId);

            return PaymentResult.builder()
                .success(true)
                .transactionId(refundTxnId)
                .gatewayResponse(response)
                .build();
        } catch (Exception e) {
            log.error("Wallet refund failed", e);
            return PaymentResult.builder()
                .success(false)
                .failureReason(e.getMessage())
                .build();
        }
    }
}
