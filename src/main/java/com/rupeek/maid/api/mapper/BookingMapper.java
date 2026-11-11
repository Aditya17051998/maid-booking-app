package com.rupeek.maid.api.mapper;

import com.rupeek.maid.api.dto.BookingResponse;
import com.rupeek.maid.api.dto.CreateBookingRequest;
import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.Maid;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class BookingMapper {

    public Booking toEntity(CreateBookingRequest request, Maid maid) {
        Booking booking = new Booking();
        booking.setMaid(maid);
        booking.setCustomerId(request.getCustomerId());
        booking.setBookingType(request.getBookingType());
        booking.setStartDateTime(request.getStartDateTime());
        booking.setEndDateTime(request.getEndDateTime());
        booking.setRecurrencePattern(request.getRecurrencePattern());
        booking.setRecurrenceEndDate(request.getRecurrenceEndDate());
        booking.setSpecialInstructions(request.getSpecialInstructions());
        booking.setTotalAmount(BigDecimal.ZERO);
        booking.setPaidAmount(BigDecimal.ZERO);
        return booking;
    }

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
            .id(booking.getId())
            .bookingReference(booking.getBookingReference())
            .maidId(booking.getMaid().getId())
            .maidName(booking.getMaid().getName())
            .customerId(booking.getCustomerId())
            .bookingType(booking.getBookingType())
            .startDateTime(booking.getStartDateTime())
            .endDateTime(booking.getEndDateTime())
            .recurrencePattern(booking.getRecurrencePattern())
            .recurrenceEndDate(booking.getRecurrenceEndDate())
            .status(booking.getStatus())
            .totalAmount(booking.getTotalAmount())
            .paidAmount(booking.getPaidAmount())
            .specialInstructions(booking.getSpecialInstructions())
            .occurrencesCount(booking.getOccurrences() != null ? booking.getOccurrences().size() : 0)
            .build();
    }
}
