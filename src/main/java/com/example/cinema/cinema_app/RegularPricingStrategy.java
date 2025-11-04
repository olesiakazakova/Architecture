package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public class RegularPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice;
    }

    @Override
    public String getStrategyName() {
        return "NO_DISCOUNT";
    }
}

