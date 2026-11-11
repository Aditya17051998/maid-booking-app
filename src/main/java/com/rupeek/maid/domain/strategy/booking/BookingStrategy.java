package com.rupeek.maid.domain.strategy.booking;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.BookingType;
import java.time.LocalDateTime;

public interface BookingStrategy {
    BookingType getBookingType();
    void validateBooking(Booking booking);
    void processBooking(Booking booking);
    boolean isAvailable(Long maidId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
