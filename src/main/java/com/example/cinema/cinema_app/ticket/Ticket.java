package com.example.cinema.cinema_app.ticket;

import com.example.cinema.cinema_app.session.Session;
import com.example.cinema.cinema_app.ticket.strategy.*;
import com.example.cinema.cinema_app.user.User;
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
    private DiscountType discount = DiscountType.NO_DISCOUNT;

    @Column(name = "is_purchased", nullable = false)
    private boolean isPurchased = false;

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

    public UUID getSessionId() {
        return session != null ? session.getSessionId() : null;
    }

    private PricingStrategy createPricingStrategy() {
        switch (discount) {
            case STUDENT_DISCOUNT:
                return new StudentPricingStrategy();
            case CHILD_DISCOUNT:
                return new ChildPricingStrategy();
            case SENIOR_DISCOUNT:
                return new SeniorPricingStrategy();
            case NO_DISCOUNT:
            default:
                return new RegularPricingStrategy();
        }
    }

    public BigDecimal getFinalPrice() {
        try {
            if (session == null || session.getCost() == null) {
                return BigDecimal.ZERO;
            }

            PricingStrategy strategy = createPricingStrategy();
            return strategy.calculatePrice(session.getCost());

        } catch (Exception e) {
            return session != null && session.getCost() != null ? session.getCost() : BigDecimal.ZERO;
        }
    }

    @Override
    public Ticket copy() {
        Ticket copy = new Ticket();
        copy.session = this.session;
        copy.discount = this.discount;
        copy.isPurchased = this.isPurchased;
        return copy;
    }
}


