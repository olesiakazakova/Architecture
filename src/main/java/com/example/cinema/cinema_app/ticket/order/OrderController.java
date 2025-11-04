package com.example.cinema.cinema_app.ticket.order;

import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.ticket.TicketRepository;
import com.example.cinema.cinema_app.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public String showOrdersPage(Model model) {
        List<OrderComponent> allOrders = unifiedOrderService.getAllOrders();
        Map<String, Object> stats = unifiedOrderService.getOrderStatistics();

        model.addAttribute("orders", allOrders);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("availableTickets", ticketRepository.findByIsPurchasedFalse());

        model.addAllAttributes(stats);
        return "order/listOrder";
    }

    @PostMapping
    public String createOrder(@RequestParam String orderName,
                              @RequestParam String userEmail,
                              @RequestParam List<UUID> ticketIds) {
        try {
            OrderComposite order = orderService.createOrder(orderName, userEmail, ticketIds);
            return "redirect:/orders";
        } catch (Exception e) {
            return "redirect:/orders?error=" + e.getMessage();
        }
    }

    @PostMapping("/{orderId}/purchase")
    @ResponseBody
    public ResponseEntity<?> purchaseOrder(@PathVariable UUID orderId) {
        try {
            OrderComposite order = orderService.purchaseOrder(orderId);
            return ResponseEntity.ok(String.format("Заказ '%s' куплен. Общая стоимость: %.2f руб.",
                    order.getName(), order.getTotalPrice()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}/total")
    @ResponseBody
    public ResponseEntity<?> getOrderTotal(@PathVariable UUID orderId) {
        try {
            BigDecimal total = orderService.getOrderTotalPrice(orderId);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}/tickets")
    @ResponseBody
    public List<Ticket> getOrderTickets(@PathVariable UUID orderId) {
        return orderService.getOrderTickets(orderId);
    }


    @DeleteMapping("/{orderId}")
    @ResponseBody
    public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok().body("Заказ успешно удален");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userEmail}")
    @ResponseBody
    public ResponseEntity<?> getUserOrders(@PathVariable String userEmail) {
        try {
            List<OrderComposite> orders = orderService.getUserOrders(userEmail);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}