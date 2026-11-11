package com.rupeek.maid.domain.repository;

import com.rupeek.maid.domain.model.BookingOccurrence;
import com.rupeek.maid.domain.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingOccurrenceRepository {
    BookingOccurrence save(BookingOccurrence occurrence);
    Optional<BookingOccurrence> findById(Long id);
    List<BookingOccurrence> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    long count();

    List<BookingOccurrence> findByBookingId(Long bookingId);
    List<BookingOccurrence> findOverlappingOccurrences(
        Long maidId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<BookingStatus> excludedStatuses
    );
}
