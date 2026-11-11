package com.rupeek.maid.domain.strategy.cancellation;

import com.rupeek.maid.domain.model.Booking;
import java.math.BigDecimal;

public interface CancellationPolicy {
    String getPolicyName();
    BigDecimal calculateRefundAmount(Booking booking);
    boolean canCancel(Booking booking);
}
