package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(BigDecimal basePrice);
    String getStrategyName();
}
