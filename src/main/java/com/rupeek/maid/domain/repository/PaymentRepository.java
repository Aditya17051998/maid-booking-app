package com.rupeek.maid.domain.repository;

import com.rupeek.maid.domain.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();

    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    List<Payment> findByBookingId(Long bookingId);
}
