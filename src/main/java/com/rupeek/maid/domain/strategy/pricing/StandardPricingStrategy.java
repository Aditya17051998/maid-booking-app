package com.rupeek.maid.domain.strategy.pricing;

import com.rupeek.maid.domain.model.Maid;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public String getStrategyName() {
        return "STANDARD";
    }

    @Override
    public BigDecimal calculatePrice(Maid maid, LocalDateTime startTime, LocalDateTime endTime) {
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return maid.getHourlyRate().multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }
}
