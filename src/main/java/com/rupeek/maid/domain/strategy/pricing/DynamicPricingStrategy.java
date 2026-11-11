package com.rupeek.maid.domain.strategy.pricing;

import com.rupeek.maid.domain.model.Maid;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Component
public class DynamicPricingStrategy implements PricingStrategy {

    private static final BigDecimal PEAK_HOUR_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.3");
    private static final BigDecimal HIGH_DEMAND_MULTIPLIER = new BigDecimal("1.2");

    @Override
    public String getStrategyName() {
        return "DYNAMIC";
    }

    @Override
    public BigDecimal calculatePrice(Maid maid, LocalDateTime startTime, LocalDateTime endTime) {
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal basePrice = maid.getHourlyRate().multiply(hours);

        BigDecimal multiplier = BigDecimal.ONE;

        if (isPeakHour(startTime)) {
            multiplier = multiplier.multiply(PEAK_HOUR_MULTIPLIER);
        }

        if (isWeekend(startTime)) {
            multiplier = multiplier.multiply(WEEKEND_MULTIPLIER);
        }

        if (isHighDemandMaid(maid)) {
            multiplier = multiplier.multiply(HIGH_DEMAND_MULTIPLIER);
        }

        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isPeakHour(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        return (time.isAfter(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(10, 0))) ||
               (time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(21, 0)));
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isHighDemandMaid(Maid maid) {
        return maid.getRating() >= 4.5 && maid.getTotalReviews() >= 10;
    }
}
