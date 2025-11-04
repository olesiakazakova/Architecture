package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public class SeniorPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.6"));
    }

    @Override
    public String getStrategyName() {
        return "SENIOR_DISCOUNT";
    }
}
