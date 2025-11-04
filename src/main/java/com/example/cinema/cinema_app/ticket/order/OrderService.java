package com.example.cinema.cinema_app.ticket.order;

import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.ticket.TicketRepository;
import com.example.cinema.cinema_app.user.User;
import com.example.cinema.cinema_app.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public List<OrderComposite> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderComposite> getUserOrders(String userEmail) {
        return orderRepository.findByUser_Email(userEmail);
    }

    public void deleteOrder(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Заказ не найден: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    @Transactional
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
            ticket.setIsPurchased(true);
        }

        order.setTickets(tickets);

        OrderComposite savedOrder = orderRepository.save(order);

        ticketRepository.saveAll(tickets);

        return savedOrder;
    }

    public OrderComposite getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));
    }

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

    public BigDecimal getOrderTotalPrice(UUID orderId) {
        OrderComposite order = getOrderById(orderId);
        return order.getTotalPrice();
    }

    public List<Ticket> getOrderTickets(UUID orderId) {
        OrderComposite order = getOrderById(orderId);
        return order.getAllTickets();
    }

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