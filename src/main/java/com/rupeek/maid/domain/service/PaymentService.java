package com.rupeek.maid.domain.service;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.Payment;
import com.rupeek.maid.domain.model.PaymentMethodType;
import com.rupeek.maid.domain.model.PaymentStatus;
import com.rupeek.maid.domain.repository.PaymentRepository;
import com.rupeek.maid.domain.strategy.payment.PaymentMethod;
import com.rupeek.maid.domain.strategy.payment.PaymentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private List<PaymentMethod> paymentMethods;

    private Map<PaymentMethodType, PaymentMethod> methodMap;

    private Map<PaymentMethodType, PaymentMethod> getMethodMap() {
        if (methodMap == null) {
            methodMap = paymentMethods.stream()
                .collect(Collectors.toMap(PaymentMethod::getType, Function.identity()));
        }
        return methodMap;
    }

    @Transactional
    public Payment processPayment(Long bookingId, PaymentMethodType paymentMethodType, BigDecimal amount, String idempotencyKey) {
        if (idempotencyKey != null) {
            return paymentRepository.findByIdempotencyKey(idempotencyKey)
                .orElseGet(() -> processPaymentInternal(bookingId, paymentMethodType, amount, idempotencyKey));
        }
        return processPaymentInternal(bookingId, paymentMethodType, amount, UUID.randomUUID().toString());
    }

    private Payment processPaymentInternal(Long bookingId, PaymentMethodType paymentMethodType, BigDecimal amount, String idempotencyKey) {
        log.info("Processing payment for booking: {}, method: {}, amount: {}", bookingId, paymentMethodType, amount);

        Booking booking = bookingService.getBookingById(bookingId);

        if (booking.getPaidAmount().add(amount).compareTo(booking.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds booking total");
        }

        PaymentMethod paymentMethod = getMethodMap().get(paymentMethodType);
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethodType);
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTransactionId(generateTransactionId());
        payment.setPaymentMethod(paymentMethodType);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setIdempotencyKey(idempotencyKey);

        payment = paymentRepository.save(payment);

        try {
            PaymentResult result = paymentMethod.processPayment(payment);

            if (result.isSuccess()) {
                payment.markAsCompleted(result.getGatewayResponse());
                booking.setPaidAmount(booking.getPaidAmount().add(amount));

                if (booking.getPaidAmount().compareTo(booking.getTotalAmount()) >= 0) {
                    bookingService.confirmBooking(booking.getId());
                }
            } else {
                payment.markAsFailed(result.getFailureReason());
            }

            return paymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            payment.markAsFailed(e.getMessage());
            return paymentRepository.save(payment);
        }
    }

    @Transactional
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (!payment.canBeRefunded()) {
            throw new IllegalStateException("Payment cannot be refunded");
        }

        PaymentMethod paymentMethod = getMethodMap().get(payment.getPaymentMethod());
        PaymentResult result = paymentMethod.refund(payment);

        if (result.isSuccess()) {
            payment.markAsRefunded();
            Booking booking = payment.getBooking();
            booking.setPaidAmount(booking.getPaidAmount().subtract(payment.getAmount()));
        }

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Payment> getBookingPayments(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
