package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public class ChildPricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.5"));
    }

    @Override
    public String getStrategyName() {
        return "CHILD_DISCOUNT";
    }
}
