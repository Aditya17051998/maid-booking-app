package com.rupeek.maid.domain.strategy.payment;

import com.rupeek.maid.domain.model.Payment;
import com.rupeek.maid.domain.model.PaymentMethodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CardPaymentMethodTest {

    private CardPaymentMethod cardPaymentMethod;
    private Payment payment;

    @BeforeEach
    void setUp() {
        cardPaymentMethod = new CardPaymentMethod();
        payment = new Payment();
        payment.setTransactionId("TEST-TXN-001");
        payment.setAmount(new BigDecimal("500.00"));
    }

    @Test
    void testGetType() {
        assertEquals(PaymentMethodType.CARD, cardPaymentMethod.getType());
    }

    @Test
    void testProcessPayment_Success() {
        PaymentResult result = cardPaymentMethod.processPayment(payment);

        assertTrue(result.isSuccess());
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("CARD-"));
        assertNotNull(result.getGatewayResponse());
        assertNull(result.getFailureReason());
    }

    @Test
    void testRefund_Success() {
        PaymentResult result = cardPaymentMethod.refund(payment);

        assertTrue(result.isSuccess());
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("REFUND-"));
        assertNotNull(result.getGatewayResponse());
        assertNull(result.getFailureReason());
    }
}
