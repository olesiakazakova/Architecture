package com.example.cinema.cinema_app;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Базовый интерфейс для всех компонентов заказа
public interface OrderComponent {
    UUID getId();
    String getName();
    BigDecimal getTotalPrice();
    String getDescription();
    List<Ticket> getAllTickets();
    boolean isPurchased();
    void markAsPurchased();
    User getUser();

    default String getType() {
        if (this instanceof OrderComposite) {
            return "COMPOSITE";
        } else if (this instanceof TicketAdapter) {
            return "SINGLE";
        }
        return "UNKNOWN";
    }

    default boolean isComposite() {
        return getType().equals("COMPOSITE");
    }

    default boolean isSingle() {
        return getType().equals("SINGLE");
    }

    // Композитные методы
    default void addComponent(OrderComponent component) {
        throw new UnsupportedOperationException("Операция не поддерживается");
    }

    default void removeComponent(OrderComponent component) {
        throw new UnsupportedOperationException("Операция не поддерживается");
    }

    default List<OrderComponent> getChildren() {
        return Collections.emptyList();
    }
}
