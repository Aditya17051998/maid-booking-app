package com.rupeek.maid.api.mapper;

import com.rupeek.maid.api.dto.PaymentResponse;
import com.rupeek.maid.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
            .id(payment.getId())
            .transactionId(payment.getTransactionId())
            .bookingId(payment.getBooking().getId())
            .paymentMethod(payment.getPaymentMethod())
            .amount(payment.getAmount())
            .status(payment.getStatus())
            .gatewayResponse(payment.getPaymentGatewayResponse())
            .failureReason(payment.getFailureReason())
            .createdAt(payment.getCreatedAt())
            .build();
    }
}
