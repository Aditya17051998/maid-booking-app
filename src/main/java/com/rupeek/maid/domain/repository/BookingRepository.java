package com.rupeek.maid.domain.repository;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();

    Optional<Booking> findByBookingReference(String bookingReference);
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByMaidId(Long maidId);
    List<Booking> findOverlappingBookings(
        Long maidId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<BookingStatus> excludedStatuses
    );
}
