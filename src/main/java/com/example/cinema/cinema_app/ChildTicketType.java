package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public class ChildTicketType implements TicketType {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.5")); // 50% скидка
    }

    @Override
    public String getTicketTypeName() {
        return "Детский билет";
    }

    @Override
    public void displayTicketInfo() {
        System.out.println("Детский билет - скидка 50%");
    }
}