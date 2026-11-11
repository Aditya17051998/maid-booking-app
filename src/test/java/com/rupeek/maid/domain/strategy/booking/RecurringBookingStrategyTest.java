package com.rupeek.maid.domain.strategy.booking;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.BookingType;
import com.rupeek.maid.domain.repository.BookingOccurrenceRepository;
import com.rupeek.maid.domain.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecurringBookingStrategyTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingOccurrenceRepository occurrenceRepository;

    @InjectMocks
    private RecurringBookingStrategy recurringBookingStrategy;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setStartDateTime(LocalDateTime.now().plusDays(1));
        booking.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(2));
        booking.setRecurrencePattern("DAILY");
        booking.setRecurrenceEndDate(LocalDateTime.now().plusDays(7));
    }

    @Test
    void testGetBookingType() {
        assertEquals(BookingType.RECURRING, recurringBookingStrategy.getBookingType());
    }

    @Test
    void testValidateBooking_Success() {
        assertDoesNotThrow(() -> recurringBookingStrategy.validateBooking(booking));
    }

    @Test
    void testValidateBooking_NoPattern() {
        booking.setRecurrencePattern(null);

        assertThrows(IllegalArgumentException.class, () ->
            recurringBookingStrategy.validateBooking(booking)
        );
    }

    @Test
    void testValidateBooking_NoEndDate() {
        booking.setRecurrenceEndDate(null);

        assertThrows(IllegalArgumentException.class, () ->
            recurringBookingStrategy.validateBooking(booking)
        );
    }

    @Test
    void testProcessBooking_Daily() {
        recurringBookingStrategy.processBooking(booking);

        assertEquals(BookingType.RECURRING, booking.getBookingType());
        assertFalse(booking.getOccurrences().isEmpty());
        assertEquals(7, booking.getOccurrences().size());
    }

    @Test
    void testProcessBooking_Weekdays() {
        booking.setRecurrencePattern("WEEKDAYS");
        booking.setStartDateTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        booking.setEndDateTime(LocalDateTime.of(2024, 1, 1, 11, 0));
        booking.setRecurrenceEndDate(LocalDateTime.of(2024, 1, 7, 23, 59));

        recurringBookingStrategy.processBooking(booking);

        assertEquals(BookingType.RECURRING, booking.getBookingType());
        assertEquals(5, booking.getOccurrences().size());
    }
}
