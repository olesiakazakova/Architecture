package com.example.cinema.cinema_app.ticket.strategy;

import java.math.BigDecimal;

public class StudentPricingStrategy implements PricingStrategy {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.85"));
    }

    @Override
    public String getStrategyName() {
        return "STUDENT_DISCOUNT";
    }
}