package com.rupeek.maid.domain.strategy.payment;

import com.rupeek.maid.domain.model.Payment;
import com.rupeek.maid.domain.model.PaymentMethodType;

public interface PaymentMethod {
    PaymentMethodType getType();
    PaymentResult processPayment(Payment payment);
    PaymentResult refund(Payment payment);
}
