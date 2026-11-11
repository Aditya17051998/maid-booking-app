package com.rupeek.maid.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"occurrences", "payments"})
public class Booking {

    private Long id;
    private String bookingReference;
    private Maid maid;
    private Long customerId;
    private BookingType bookingType;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String recurrencePattern;
    private LocalDateTime recurrenceEndDate;
    private BookingStatus status = BookingStatus.PENDING;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String specialInstructions;
    private Set<BookingOccurrence> occurrences = new HashSet<>();
    private Set<Payment> payments = new HashSet<>();
    private String idempotencyKey;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addOccurrence(BookingOccurrence occurrence) {
        this.occurrences.add(occurrence);
        occurrence.setBooking(this);
    }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setBooking(this);
    }

    public boolean canBeCancelled() {
        return this.status == BookingStatus.PENDING || this.status == BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled in current status: " + this.status);
        }
        this.status = BookingStatus.CANCELLED;
    }

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
    }

    public boolean isRecurring() {
        return this.bookingType == BookingType.RECURRING;
    }
}
