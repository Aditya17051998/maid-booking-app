package com.rupeek.maid.domain.strategy.booking;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.BookingOccurrence;
import com.rupeek.maid.domain.model.BookingStatus;
import com.rupeek.maid.domain.model.BookingType;
import com.rupeek.maid.domain.repository.BookingOccurrenceRepository;
import com.rupeek.maid.domain.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class InstantBookingStrategy implements BookingStrategy {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingOccurrenceRepository occurrenceRepository;

    @Override
    public BookingType getBookingType() {
        return BookingType.INSTANT;
    }

    @Override
    public void validateBooking(Booking booking) {
        LocalDateTime now = LocalDateTime.now();
        if (booking.getStartDateTime().isBefore(now.minusMinutes(5))) {
            throw new IllegalArgumentException("Instant booking start time must be current time or within next few minutes");
        }
    }

    @Override
    public void processBooking(Booking booking) {
        booking.setBookingType(BookingType.INSTANT);
    }

    @Override
    public boolean isAvailable(Long maidId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<BookingStatus> excludedStatuses = List.of(BookingStatus.CANCELLED);

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
            maidId, startDateTime, endDateTime, excludedStatuses
        );

        List<BookingOccurrence> overlappingOccurrences = occurrenceRepository.findOverlappingOccurrences(
            maidId, startDateTime, endDateTime, excludedStatuses
        );

        return overlappingBookings.isEmpty() && overlappingOccurrences.isEmpty();
    }
}
