package com.example.cinema.cinema_app.ticket.factory;

import java.math.BigDecimal;

public class SeniorTicketType implements TicketType {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.6"));
    }

    @Override
    public String getTicketTypeName() {
        return "Билет для пенсионеров";
    }
}