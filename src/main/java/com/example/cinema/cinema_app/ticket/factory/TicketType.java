package com.example.cinema.cinema_app.ticket.factory;

import java.math.BigDecimal;

public interface TicketType {
    BigDecimal calculatePrice(BigDecimal basePrice);
    String getTicketTypeName();
}