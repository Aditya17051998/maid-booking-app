package com.rupeek.maid.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"booking"})
public class BookingOccurrence {

    private Long id;
    private Booking booking;
    private LocalDateTime occurrenceStartTime;
    private LocalDateTime occurrenceEndTime;
    private BookingStatus status = BookingStatus.PENDING;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void cancel() {
        if (this.status != BookingStatus.PENDING && this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Occurrence cannot be cancelled in current status: " + this.status);
        }
        this.status = BookingStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status != BookingStatus.CANCELLED;
    }
}
