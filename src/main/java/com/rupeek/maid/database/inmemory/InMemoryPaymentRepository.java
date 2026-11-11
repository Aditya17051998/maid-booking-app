package com.rupeek.maid.database.inmemory;

import com.rupeek.maid.domain.model.Payment;
import com.rupeek.maid.domain.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<Long, Payment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(idGenerator.getAndIncrement());
            payment.setCreatedAt(LocalDateTime.now());
        }
        payment.setUpdatedAt(LocalDateTime.now());
        storage.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return storage.values().stream()
            .filter(payment -> payment.getTransactionId().equals(transactionId))
            .findFirst();
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null) {
            return Optional.empty();
        }
        return storage.values().stream()
            .filter(payment -> idempotencyKey.equals(payment.getIdempotencyKey()))
            .findFirst();
    }

    @Override
    public List<Payment> findByBookingId(Long bookingId) {
        return storage.values().stream()
            .filter(payment -> payment.getBooking().getId().equals(bookingId))
            .collect(Collectors.toList());
    }
}
