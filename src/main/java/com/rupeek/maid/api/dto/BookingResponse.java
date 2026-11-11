package com.rupeek.maid.api.dto;

import com.rupeek.maid.domain.model.BookingStatus;
import com.rupeek.maid.domain.model.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingReference;
    private Long maidId;
    private String maidName;
    private Long customerId;
    private BookingType bookingType;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String recurrencePattern;
    private LocalDateTime recurrenceEndDate;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String specialInstructions;
    private Integer occurrencesCount;
}
