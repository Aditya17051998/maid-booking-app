package com.rupeek.maid.database.inmemory;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.BookingStatus;
import com.rupeek.maid.domain.repository.BookingRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {

    private final Map<Long, Booking> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idGenerator.getAndIncrement());
            booking.setCreatedAt(LocalDateTime.now());
        }
        booking.setUpdatedAt(LocalDateTime.now());
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Booking> findAll() {
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
    public Optional<Booking> findByBookingReference(String bookingReference) {
        return storage.values().stream()
            .filter(booking -> booking.getBookingReference().equals(bookingReference))
            .findFirst();
    }

    @Override
    public Optional<Booking> findByIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null) {
            return Optional.empty();
        }
        return storage.values().stream()
            .filter(booking -> idempotencyKey.equals(booking.getIdempotencyKey()))
            .findFirst();
    }

    @Override
    public List<Booking> findByCustomerId(Long customerId) {
        return storage.values().stream()
            .filter(booking -> booking.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByMaidId(Long maidId) {
        return storage.values().stream()
            .filter(booking -> booking.getMaid().getId().equals(maidId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findOverlappingBookings(
        Long maidId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<BookingStatus> excludedStatuses
    ) {
        return storage.values().stream()
            .filter(booking -> booking.getMaid().getId().equals(maidId))
            .filter(booking -> !excludedStatuses.contains(booking.getStatus()))
            .filter(booking -> isOverlapping(booking.getStartDateTime(), booking.getEndDateTime(), startTime, endTime))
            .collect(Collectors.toList());
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
