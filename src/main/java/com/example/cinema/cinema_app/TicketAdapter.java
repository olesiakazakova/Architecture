package com.example.cinema.cinema_app;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TicketAdapter implements OrderComponent {
    private final Ticket ticket;

    public TicketAdapter(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public UUID getId() {
        return ticket.getTicketId();
    }

    @Override
    public String getName() {
        return String.format("Билет %d-%d", ticket.getRow(), ticket.getSeat());
    }

    @Override
    public BigDecimal getTotalPrice() {
        return ticket.getFinalPrice();
    }

    @Override
    public String getDescription() {
        String filmName = "Неизвестный фильм";
        if (ticket.getSession() != null && ticket.getSession().getFilm() != null) {
            filmName = ticket.getSession().getFilm().getName();
        }
        return String.format("Билет: Ряд %d, Место %d - %s",
                ticket.getRow(), ticket.getSeat(), filmName);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return Collections.singletonList(ticket);
    }

    @Override
    public boolean isPurchased() {
        return ticket.getIsPurchased();
    }

    @Override
    public void markAsPurchased() {
        ticket.setIsPurchased(true);
    }

    @Override
    public User getUser() {
        return ticket.getUser();
    }

    public Ticket getTicket() {
        return ticket;
    }

    public UUID getTicketId() {
        return ticket.getTicketId();
    }

    public int getRow() {
        return ticket.getRow();
    }

    public int getSeat() {
        return ticket.getSeat();
    }

    public Session getSession() {
        return ticket.getSession();
    }
}