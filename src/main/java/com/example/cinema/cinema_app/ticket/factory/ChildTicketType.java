package com.example.cinema.cinema_app.ticket.factory;

import java.math.BigDecimal;

public class ChildTicketType implements TicketType {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice.multiply(new BigDecimal("0.5"));
    }

    @Override
    public String getTicketTypeName() {
        return "Детский билет";
    }
}
