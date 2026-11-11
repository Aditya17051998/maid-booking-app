package com.rupeek.maid.domain.strategy.booking;

import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.BookingStatus;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstantBookingStrategyTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingOccurrenceRepository occurrenceRepository;

    @InjectMocks
    private InstantBookingStrategy instantBookingStrategy;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setStartDateTime(LocalDateTime.now());
        booking.setEndDateTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    void testGetBookingType() {
        assertEquals(BookingType.INSTANT, instantBookingStrategy.getBookingType());
    }

    @Test
    void testValidateBooking_Success() {
        assertDoesNotThrow(() -> instantBookingStrategy.validateBooking(booking));
    }

    @Test
    void testValidateBooking_PastTime() {
        booking.setStartDateTime(LocalDateTime.now().minusHours(1));

        assertThrows(IllegalArgumentException.class, () ->
            instantBookingStrategy.validateBooking(booking)
        );
    }

    @Test
    void testProcessBooking() {
        instantBookingStrategy.processBooking(booking);
        assertEquals(BookingType.INSTANT, booking.getBookingType());
    }

    @Test
    void testIsAvailable_NoOverlapping() {
        Long maidId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        when(bookingRepository.findOverlappingBookings(
            eq(maidId), eq(start), eq(end), any()
        )).thenReturn(Collections.emptyList());

        when(occurrenceRepository.findOverlappingOccurrences(
            eq(maidId), eq(start), eq(end), any()
        )).thenReturn(Collections.emptyList());

        assertTrue(instantBookingStrategy.isAvailable(maidId, start, end));
    }

    @Test
    void testIsAvailable_HasOverlapping() {
        Long maidId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        Booking overlappingBooking = new Booking();
        overlappingBooking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findOverlappingBookings(
            eq(maidId), eq(start), eq(end), any()
        )).thenReturn(List.of(overlappingBooking));

        assertFalse(instantBookingStrategy.isAvailable(maidId, start, end));
    }
}
