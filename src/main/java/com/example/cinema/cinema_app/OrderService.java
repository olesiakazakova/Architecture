package com.example.cinema.cinema_app;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Получение всех заказов
     */
    public List<OrderComposite> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Получение заказов пользователя
     */
    public List<OrderComposite> getUserOrders(String userEmail) {
        return orderRepository.findByUser_Email(userEmail);
    }

    /**
     * Удаление заказа
     */
    public void deleteOrder(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Заказ не найден: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    /**
     * Создание заказа из билетов
     */
    public OrderComposite createOrder(String orderName, String userEmail, List<UUID> ticketIds) {
        User user = userRepository.findById(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userEmail));

        OrderComposite order = new OrderComposite(orderName, user);

        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        if (tickets.size() != ticketIds.size()) {
            throw new IllegalArgumentException("Некоторые билеты не найдены");
        }

        for (Ticket ticket : tickets) {
            if (ticket.getIsPurchased()) {
                throw new IllegalStateException("Билет уже куплен: " + ticket.getTicketId());
            }
        }

        order.setTickets(tickets);
        return orderRepository.save(order);
    }

    /**
     * Получение заказа с загруженными компонентами
     */
    public OrderComposite getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));
    }

    /**
     * Покупка заказа
     */
    public OrderComposite purchaseOrder(UUID orderId) {
        OrderComposite order = getOrderById(orderId);

        if (order.isPurchased()) {
            throw new IllegalStateException("Заказ уже куплен");
        }

        order.markAsPurchased();
        OrderComposite savedOrder = orderRepository.save(order);
        ticketRepository.saveAll(order.getTickets());

        return savedOrder;
    }

    /**
     * Получение общей стоимости заказа
     */
    public BigDecimal getOrderTotalPrice(UUID orderId) {
        OrderComposite order = getOrderById(orderId);
        return order.getTotalPrice();
    }

    /**
     * Получение всех билетов заказа
     */
    public List<Ticket> getOrderTickets(UUID orderId) {
        OrderComposite order = getOrderById(orderId);
        return order.getAllTickets();
    }

    /**
     * Добавление билета в существующий заказ
     */
    public OrderComposite addTicketToOrder(UUID orderId, UUID ticketId) {
        OrderComposite order = getOrderById(orderId);

        if (order.isPurchased()) {
            throw new IllegalStateException("Нельзя изменить купленный заказ");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Билет не найден: " + ticketId));

        if (ticket.getIsPurchased()) {
            throw new IllegalStateException("Билет уже куплен: " + ticketId);
        }

        order.addTicket(ticket);
        return orderRepository.save(order);
    }

    /**
     * Удаление билета из заказа
     */
    public OrderComposite removeTicketFromOrder(UUID orderId, UUID ticketId) {
        OrderComposite order = getOrderById(orderId);

        if (order.isPurchased()) {
            throw new IllegalStateException("Нельзя изменить купленный заказ");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Билет не найден: " + ticketId));

        order.removeTicket(ticket);
        return orderRepository.save(order);
    }
}