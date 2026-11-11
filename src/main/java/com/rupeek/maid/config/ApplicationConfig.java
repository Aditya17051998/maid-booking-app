package com.rupeek.maid.config;

import com.rupeek.maid.domain.strategy.cancellation.CancellationPolicy;
import com.rupeek.maid.domain.strategy.cancellation.StandardCancellationPolicy;
import com.rupeek.maid.domain.strategy.pricing.DynamicPricingStrategy;
import com.rupeek.maid.domain.strategy.pricing.PricingStrategy;
import com.rupeek.maid.domain.strategy.pricing.StandardPricingStrategy;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ApplicationConfig {

    @Value("${pricing.strategy:STANDARD}")
    private String pricingStrategyType;

    @Bean
    @Primary
    public PricingStrategy pricingStrategy(
        StandardPricingStrategy standardStrategy,
        DynamicPricingStrategy dynamicStrategy
    ) {
        if ("DYNAMIC".equalsIgnoreCase(pricingStrategyType)) {
            return dynamicStrategy;
        }
        return standardStrategy;
    }

    @Bean
    public CancellationPolicy cancellationPolicy(StandardCancellationPolicy policy) {
        return policy;
    }
}
