package com.example.cinema.cinema_app;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID ticketId;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int row;
    private int seat;

    @Enumerated(EnumType.STRING)
    private DiscountType discount;

    // Добавляем transient поле для фабрики
    @Transient
    private TicketTypeFactory ticketTypeFactory;

    // Метод для получения типа билета
    public TicketType getTicketType() {
        if (ticketTypeFactory == null) {
            // Можно создать фабрику здесь или бросить исключение
            throw new IllegalStateException("TicketTypeFactory not set");
        }
        return ticketTypeFactory.createTicketType(this.discount);
    }

    // Метод для получения итоговой цены
    public BigDecimal getFinalPrice() {
        if (session == null || session.getCost() == null) {
            return BigDecimal.ZERO;
        }

        TicketType ticketType = getTicketType();
        return ticketType.calculatePrice(session.getCost());
    }

    // Дополнительный метод для получения информации
    public void displayTicketInfo() {
        TicketType ticketType = getTicketType();
        ticketType.displayTicketInfo();
    }

    // Сеттер для фабрики
    public void setTicketTypeFactory(TicketTypeFactory ticketTypeFactory) {
        this.ticketTypeFactory = ticketTypeFactory;
    }

    // Геттеры и сеттеры
    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public DiscountType getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountType discount) {
        this.discount = discount;
    }

    public UUID getSessionId() {
        return session != null ? session.getSessionId() : null;
    }
}


