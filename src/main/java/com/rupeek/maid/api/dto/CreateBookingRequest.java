package com.rupeek.maid.api.dto;

import com.rupeek.maid.domain.model.BookingType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "Maid ID is required")
    private Long maidId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Booking type is required")
    private BookingType bookingType;

    @NotNull(message = "Start date time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date time is required")
    private LocalDateTime endDateTime;

    private String recurrencePattern;

    private LocalDateTime recurrenceEndDate;

    private String specialInstructions;

    private String idempotencyKey;
}
