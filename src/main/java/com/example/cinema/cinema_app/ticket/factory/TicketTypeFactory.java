package com.example.cinema.cinema_app.ticket.factory;

import com.example.cinema.cinema_app.ticket.DiscountType;
import org.springframework.stereotype.Component;

@Component
class TicketTypeFactory {
    public TicketType createTicketType(DiscountType discountType) {
        if (discountType == null) {
            return new RegularTicketType();
        }

        switch (discountType) {
            case STUDENT_DISCOUNT:
                return new StudentTicketType();
            case CHILD_DISCOUNT:
                return new ChildTicketType();
            case SENIOR_DISCOUNT:
                return new SeniorTicketType();
            case NO_DISCOUNT:
            default:
                return new RegularTicketType();
        }
    }
}