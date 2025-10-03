package com.example.cinema.cinema_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TicketTypeFactory ticketTypeFactory;

    @GetMapping
    public String getAllTickets(Model model,
                                @RequestParam(required = false) UUID sessionId) {
        try {
            List<Ticket> tickets;

            if (sessionId != null) {
                tickets = ticketRepository.findBySession_SessionIdOrderByRowAscSeatAsc(sessionId);

                tickets.forEach(ticket -> ticket.setTicketTypeFactory(ticketTypeFactory));

                Map<Integer, List<Ticket>> seatingChart = sessionService.getHallSeatingChart(sessionId);
                model.addAttribute("seatingChart", seatingChart);

                Map<String, Object> stats = sessionService.getSessionStats(sessionId);
                model.addAttribute("stats", stats);

                Session cinemaSession = sessionService.findById(sessionId);
                model.addAttribute("cinemaSession", cinemaSession);
            } else {
                tickets = ticketRepository.findAll();
                tickets.forEach(ticket -> ticket.setTicketTypeFactory(ticketTypeFactory));
            }

            model.addAttribute("tickets", tickets);
            model.addAttribute("cinemaSessions", sessionRepository.findAll());
            model.addAttribute("selectedSessionId", sessionId);
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("discountTypes", DiscountType.values());

            return "session/listTickets";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке билетов: " + e.getMessage());
            return "session/listTickets";
        }
    }

    @PostMapping("/purchase")
    public String purchaseTicket(@RequestParam UUID ticketId,
                                 @RequestParam String userEmail,
                                 @RequestParam DiscountType discountType,
                                 RedirectAttributes redirectAttributes) {

        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Билет не найден"));

            User user = userRepository.findById(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            if (ticket.getIsPurchased()) {
                redirectAttributes.addFlashAttribute("error", "Этот билет уже куплен");
                return "redirect:/tickets?sessionId=" + ticket.getSession().getSessionId();
            }

            TicketType ticketType = ticketTypeFactory.createTicketType(discountType);
            BigDecimal finalPrice = BigDecimal.ZERO;

            if (ticket.getSession() != null && ticket.getSession().getCost() != null) {
                finalPrice = ticketType.calculatePrice(ticket.getSession().getCost());
            }

            ticket.setUser(user);
            ticket.setDiscount(discountType);
            ticket.setIsPurchased(true);
            ticket.setTicketTypeFactory(ticketTypeFactory);

            ticketRepository.save(ticket);

            redirectAttributes.addFlashAttribute("success",
                    "Билет успешно куплен! " + ticketType.getTicketTypeName() +
                            " - Цена: " + finalPrice + " ₽");

            return "redirect:/tickets?sessionId=" + ticket.getSession().getSessionId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при покупке билета: " + e.getMessage());
            return "redirect:/tickets";
        }
    }

    @PostMapping("/cancel")
    public String cancelTicket(@RequestParam UUID ticketId,
                               RedirectAttributes redirectAttributes) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

            UUID sessionId = ticket.getSession().getSessionId();

            ticket.setUser(null);
            ticket.setDiscount(DiscountType.NO_DISCOUNT);
            ticket.setIsPurchased(false);

            ticketRepository.save(ticket);

            redirectAttributes.addFlashAttribute("success", "Покупка билета отменена");
            return "redirect:/tickets?sessionId=" + sessionId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при отмене билета: " + e.getMessage());
            return "redirect:/tickets";
        }
    }

    @GetMapping("/calculate-price")
    @ResponseBody
    public Map<String, Object> calculatePrice(@RequestParam BigDecimal basePrice,
                                              @RequestParam DiscountType discountType) {
        Map<String, Object> result = new HashMap<>();

        try {
            TicketType ticketType = ticketTypeFactory.createTicketType(discountType);
            BigDecimal finalPrice = ticketType.calculatePrice(basePrice);
            BigDecimal discountAmount = basePrice.subtract(finalPrice);

            result.put("success", true);
            result.put("basePrice", basePrice);
            result.put("finalPrice", finalPrice);
            result.put("discountAmount", discountAmount);
            result.put("discountPercent", discountAmount.divide(basePrice, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            result.put("ticketTypeName", ticketType.getTicketTypeName());

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }
}