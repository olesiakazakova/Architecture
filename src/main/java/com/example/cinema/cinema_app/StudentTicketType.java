package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public class StudentTicketType implements TicketType {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.7")); // 30% скидка
    }

    @Override
    public String getTicketTypeName() {
        return "Студенческий билет";
    }

    @Override
    public void displayTicketInfo() {
        System.out.println("Студенческий билет - скидка 30%");
    }
}
