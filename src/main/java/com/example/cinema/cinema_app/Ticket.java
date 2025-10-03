package com.example.cinema.cinema_app;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket implements Copyable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID ticketId;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int row;
    private int seat;

    @Enumerated(EnumType.STRING)
    private DiscountType discount;

    @Column(name = "is_purchased", nullable = false)
    private boolean isPurchased = false;

    @Transient
    private TicketTypeFactory ticketTypeFactory;

    public Ticket() {}

    public Ticket(Session session, int row, int seat) {
        this.session = session;
        this.row = row;
        this.seat = seat;
        this.discount = DiscountType.NO_DISCOUNT;
        this.isPurchased = false;
        this.user = null;
    }

    public UUID getTicketId() { return ticketId; }
    public void setTicketId(UUID ticketId) { this.ticketId = ticketId; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getSeat() { return seat; }
    public void setSeat(int seat) { this.seat = seat; }

    public DiscountType getDiscount() { return discount; }
    public void setDiscount(DiscountType discount) { this.discount = discount; }

    public boolean getIsPurchased() { return isPurchased; }
    public void setIsPurchased(boolean isPurchased) { this.isPurchased = isPurchased; }

    public TicketTypeFactory getTicketTypeFactory() { return ticketTypeFactory; }
    public void setTicketTypeFactory(TicketTypeFactory ticketTypeFactory) {
        this.ticketTypeFactory = ticketTypeFactory;
    }

    public TicketType getTicketType() {
        if (ticketTypeFactory == null) {
            throw new IllegalStateException("TicketTypeFactory not set");
        }
        return ticketTypeFactory.createTicketType(this.discount);
    }

    public BigDecimal getFinalPrice() {
        if (session == null || session.getCost() == null) {
            return BigDecimal.ZERO;
        }
        TicketType ticketType = getTicketType();
        return ticketType.calculatePrice(session.getCost());
    }

    public UUID getSessionId() {
        return session != null ? session.getSessionId() : null;
    }

    @Override
    public Ticket copy() {
        Ticket copy = new Ticket();
        copy.session = this.session;
        copy.discount = this.discount;
        copy.isPurchased = this.isPurchased;
        copy.ticketTypeFactory = this.ticketTypeFactory;
        return copy;
    }
}


