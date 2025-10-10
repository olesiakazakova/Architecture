package com.example.cinema.cinema_app;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
public class OrderComposite implements OrderComponent {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id")
    private UUID id;

    @Column(name = "order_name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_purchased", nullable = false)
    private boolean purchased = false;

    // Прямая связь с билетами через JPA
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "order_tickets",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "ticket_id")
    )
    private List<Ticket> tickets = new ArrayList<>();

    @Transient
    private List<OrderComponent> components = new ArrayList<>();

    public OrderComposite() {
        this.createdAt = LocalDateTime.now();
    }

    public OrderComposite(String name, User user) {
        this();
        this.name = name;
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void initializeComponents() {
        // Автоматически создаем адаптеры для билетов
        components.clear();
        if (tickets != null) {
            for (Ticket ticket : tickets) {
                components.add(new TicketAdapter(ticket));
            }
        }
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public BigDecimal getTotalPrice() {
        initializeComponents();
        return components.stream()
                .map(OrderComponent::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String getDescription() {
        initializeComponents();
        if (description != null && !description.isEmpty()) {
            return description;
        }
        return String.format("Заказ '%s': %d элементов, %.2f руб.",
                name, components.size(), getTotalPrice());
    }

    @Override
    public List<Ticket> getAllTickets() {
        initializeComponents();
        return components.stream()
                .flatMap(component -> component.getAllTickets().stream())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPurchased() { return purchased; }

    @Override
    public void markAsPurchased() {
        this.purchased = true;
        initializeComponents();
        components.forEach(OrderComponent::markAsPurchased);

        // Также помечаем билеты как купленные
        if (tickets != null) {
            tickets.forEach(ticket -> ticket.setIsPurchased(true));
        }
    }

    @Override
    public User getUser() { return user; }

    @Override
    public void addComponent(OrderComponent component) {
        initializeComponents();
        components.add(component);

        // Если добавляем адаптер билета, добавляем и сам билет
        if (component instanceof TicketAdapter) {
            TicketAdapter adapter = (TicketAdapter) component;
            tickets.add(adapter.getTicket());
        }
    }

    @Override
    public void removeComponent(OrderComponent component) {
        initializeComponents();
        components.remove(component);

        // Если удаляем адаптер билета, удаляем и сам билет
        if (component instanceof TicketAdapter) {
            TicketAdapter adapter = (TicketAdapter) component;
            tickets.remove(adapter.getTicket());
        }
    }

    @Override
    public List<OrderComponent> getChildren() {
        initializeComponents();
        return new ArrayList<>(components);
    }

    // Прямые методы для работы с билетами
    public void addTicket(Ticket ticket) {
        if (tickets == null) {
            tickets = new ArrayList<>();
        }
        tickets.add(ticket);
        components.add(new TicketAdapter(ticket));
    }

    public void removeTicket(Ticket ticket) {
        if (tickets != null) {
            tickets.remove(ticket);
        }
        components.removeIf(component ->
                component instanceof TicketAdapter &&
                        ((TicketAdapter) component).getTicket().equals(ticket)
        );
    }

    // Геттеры и сеттеры для JPA
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setUser(User user) { this.user = user; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        initializeComponents();
    }

    public List<OrderComponent> getComponents() {
        initializeComponents();
        return components;
    }

    public void setComponents(List<OrderComponent> components) {
        this.components = components;
        // Обновляем tickets на основе компонентов
        if (components != null) {
            this.tickets = components.stream()
                    .filter(component -> component instanceof TicketAdapter)
                    .map(component -> ((TicketAdapter) component).getTicket())
                    .collect(Collectors.toList());
        }
    }
}