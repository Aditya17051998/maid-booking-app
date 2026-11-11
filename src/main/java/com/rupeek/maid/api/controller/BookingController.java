package com.rupeek.maid.api.controller;

import com.rupeek.maid.api.dto.BookingResponse;
import com.rupeek.maid.api.dto.CreateBookingRequest;
import com.rupeek.maid.api.mapper.BookingMapper;
import com.rupeek.maid.domain.model.Booking;
import com.rupeek.maid.domain.model.Maid;
import com.rupeek.maid.domain.service.BookingService;
import com.rupeek.maid.domain.service.MaidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Management", description = "APIs for booking maids")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private MaidService maidService;

    @Autowired
    private BookingMapper bookingMapper;

    @PostMapping
    @Operation(summary = "Create a booking", description = "Create a new booking (instant, scheduled, or recurring)")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        Maid maid = maidService.getMaidById(request.getMaidId());
        Booking booking = bookingMapper.toEntity(request, maid);
        Booking createdBooking = bookingService.createBooking(booking, request.getIdempotencyKey());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.toResponse(createdBooking));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieve booking details by ID")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get booking by reference", description = "Retrieve booking details by reference number")
    public ResponseEntity<BookingResponse> getBookingByReference(@PathVariable String reference) {
        Booking booking = bookingService.getBookingByReference(reference);
        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer bookings", description = "Retrieve all bookings for a customer")
    public ResponseEntity<List<BookingResponse>> getCustomerBookings(@PathVariable Long customerId) {
        List<Booking> bookings = bookingService.getCustomerBookings(customerId);
        List<BookingResponse> responses = bookings.stream()
            .map(bookingMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/maid/{maidId}")
    @Operation(summary = "Get maid bookings", description = "Retrieve all bookings for a maid")
    public ResponseEntity<List<BookingResponse>> getMaidBookings(@PathVariable Long maidId) {
        List<Booking> bookings = bookingService.getMaidBookings(maidId);
        List<BookingResponse> responses = bookings.stream()
            .map(bookingMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm booking", description = "Confirm a pending booking")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        Booking booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel a booking")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        Booking booking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(bookingMapper.toResponse(booking));
    }

    @PostMapping("/occurrences/{occurrenceId}/cancel")
    @Operation(summary = "Cancel occurrence", description = "Cancel a single occurrence of a recurring booking")
    public ResponseEntity<Void> cancelOccurrence(@PathVariable Long occurrenceId) {
        bookingService.cancelOccurrence(occurrenceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/refund-amount")
    @Operation(summary = "Calculate refund amount", description = "Calculate refund amount for a booking cancellation")
    public ResponseEntity<BigDecimal> calculateRefundAmount(@PathVariable Long id) {
        BigDecimal refundAmount = bookingService.calculateRefundAmount(id);
        return ResponseEntity.ok(refundAmount);
    }
}
