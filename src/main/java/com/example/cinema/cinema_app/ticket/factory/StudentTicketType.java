package com.example.cinema.cinema_app.ticket.factory;

import java.math.BigDecimal;

public class StudentTicketType implements TicketType {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.85"));
    }

    @Override
    public String getTicketTypeName() {
        return "Студенческий билет";
    }
}
