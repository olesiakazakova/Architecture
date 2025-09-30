package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public class SeniorTicketType implements TicketType {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.6")); // 40% скидка
    }

    @Override
    public String getTicketTypeName() {
        return "Билет для пенсионеров";
    }

    @Override
    public void displayTicketInfo() {
        System.out.println("Билет для пенсионеров - скидка 40%");
    }
}