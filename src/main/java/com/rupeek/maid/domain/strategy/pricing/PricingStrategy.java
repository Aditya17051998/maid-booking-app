package com.rupeek.maid.domain.strategy.pricing;

import com.rupeek.maid.domain.model.Maid;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PricingStrategy {
    String getStrategyName();
    BigDecimal calculatePrice(Maid maid, LocalDateTime startTime, LocalDateTime endTime);
}
