package com.example.cinema.cinema_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UnifiedOrderService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TicketRepository ticketRepository;

    /**
     * Получить все заказы - и композитные и одиночные
     */
    public List<OrderComponent> getAllOrders() {
        List<OrderComponent> allOrders = new ArrayList<>();

        // 1. Композитные заказы
        List<OrderComposite> compositeOrders = orderService.getAllOrders();
        allOrders.addAll(compositeOrders);

        // 2. Одиночные заказы (билеты, купленные напрямую)
        List<Ticket> purchasedTickets = ticketRepository.findByIsPurchasedTrue();
        for (Ticket ticket : purchasedTickets) {
            // Проверяем, что билет не входит в составной заказ
            if (!isTicketInCompositeOrder(ticket, compositeOrders)) {
                allOrders.add(new TicketAdapter(ticket));
            }
        }

        // Сортируем по дате создания (новые сначала)
        allOrders.sort((o1, o2) -> {
            if (o1 instanceof OrderComposite && o2 instanceof OrderComposite) {
                return ((OrderComposite) o2).getCreatedAt()
                        .compareTo(((OrderComposite) o1).getCreatedAt());
            }
            // Для одиночных можно использовать дату сессии или текущую дату
            return 0;
        });

        return allOrders;
    }

    private boolean isTicketInCompositeOrder(Ticket ticket, List<OrderComposite> compositeOrders) {
        return compositeOrders.stream()
                .flatMap(order -> order.getAllTickets().stream())
                .anyMatch(orderTicket -> orderTicket.getTicketId().equals(ticket.getTicketId()));
    }

    /**
     * Получить статистику по всем заказам
     */
    public Map<String, Object> getOrderStatistics() {
        List<OrderComponent> allOrders = getAllOrders();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", allOrders.size());
        stats.put("activeOrders", allOrders.stream().filter(order -> !order.isPurchased()).count());

        // ВРЕМЕННО - не рассчитываем выручку
        stats.put("totalRevenue", BigDecimal.ZERO);
        stats.put("averageOrderValue", BigDecimal.ZERO);

        // Раздельная статистика
        stats.put("compositeOrdersCount", allOrders.stream()
                .filter(order -> order instanceof OrderComposite)
                .count());
        stats.put("singleOrdersCount", allOrders.stream()
                .filter(order -> order instanceof TicketAdapter)
                .count());

        return stats;
    }
}
