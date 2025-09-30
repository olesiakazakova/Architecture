package com.example.cinema.cinema_app;

import java.math.BigDecimal;

public class RegularTicketType implements TicketType {
    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice) {
        return basePrice;
    }

    @Override
    public String getTicketTypeName() {
        return "Обычный билет";
    }

    @Override
    public void displayTicketInfo() {
        System.out.println("Обычный билет - полная стоимость");
    }
}
