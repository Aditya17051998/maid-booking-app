package com.rupeek.maid.domain.strategy.cancellation;

import com.rupeek.maid.domain.model.Booking;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class StandardCancellationPolicy implements CancellationPolicy {

    private static final long FULL_REFUND_HOURS = 24;
    private static final long PARTIAL_REFUND_HOURS = 6;
    private static final BigDecimal PARTIAL_REFUND_PERCENTAGE = new BigDecimal("0.50");

    @Override
    public String getPolicyName() {
        return "STANDARD";
    }

    @Override
    public BigDecimal calculateRefundAmount(Booking booking) {
        long hoursUntilBooking = ChronoUnit.HOURS.between(LocalDateTime.now(), booking.getStartDateTime());

        if (hoursUntilBooking >= FULL_REFUND_HOURS) {
            return booking.getPaidAmount();
        } else if (hoursUntilBooking >= PARTIAL_REFUND_HOURS) {
            return booking.getPaidAmount().multiply(PARTIAL_REFUND_PERCENTAGE);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean canCancel(Booking booking) {
        return booking.canBeCancelled();
    }
}
