package com.example.cinema.cinema_app.ticket;

import com.example.cinema.cinema_app.session.Session;
import com.example.cinema.cinema_app.session.SessionRepository;
import com.example.cinema.cinema_app.session.SessionService;
import com.example.cinema.cinema_app.ticket.command.CancelTicketCommand;
import com.example.cinema.cinema_app.ticket.command.CommandResult;
import com.example.cinema.cinema_app.ticket.command.TicketCommandManager;
import com.example.cinema.cinema_app.ticket.strategy.*;
import com.example.cinema.cinema_app.user.User;
import com.example.cinema.cinema_app.user.UserRepository;
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
    private TicketCommandManager commandManager;

    @GetMapping
    public String getAllTickets(Model model,
                                @RequestParam(required = false) UUID sessionId) {
        try {
            List<Ticket> tickets;

            if (sessionId != null) {
                tickets = ticketRepository.findBySession_SessionIdOrderByRowAscSeatAsc(sessionId);

                Map<Integer, List<Ticket>> seatingChart = sessionService.getHallSeatingChart(sessionId);
                model.addAttribute("seatingChart", seatingChart);

                Map<String, Object> stats = sessionService.getSessionStats(sessionId);
                model.addAttribute("stats", stats);

                Session cinemaSession = sessionService.findById(sessionId);
                model.addAttribute("cinemaSession", cinemaSession);

                // Информация о возможности отмены для каждого билета
                Map<UUID, Boolean> canCancelMap = new HashMap<>();
                Map<UUID, String> cancelReasonMap = new HashMap<>();

                for (Ticket ticket : tickets) {
                    if (ticket.getIsPurchased()) {
                        CancelTicketCommand cancelCommand = new CancelTicketCommand(ticketRepository, sessionService).initialize(ticket);
                        canCancelMap.put(ticket.getTicketId(), cancelCommand.canExecute());
                        cancelReasonMap.put(ticket.getTicketId(), cancelCommand.getCannotExecuteReason());
                    }
                }

                model.addAttribute("canCancelMap", canCancelMap);
                model.addAttribute("cancelReasonMap", cancelReasonMap);
            } else {
                tickets = ticketRepository.findAll();
                tickets.sort((t1, t2) -> {
                    if (t1.getIsPurchased() && !t2.getIsPurchased()) {
                        return -1;
                    }
                    if (!t1.getIsPurchased() && t2.getIsPurchased()) {
                        return 1;
                    }
                    if (!t1.getIsPurchased() && !t2.getIsPurchased()) {
                        boolean canBuy1 = t1.getSession() != null && t1.getSession().canPurchaseTickets();
                        boolean canBuy2 = t2.getSession() != null && t2.getSession().canPurchaseTickets();

                        if (canBuy1 && !canBuy2) {
                            return -1;
                        }
                        if (!canBuy1 && canBuy2) {
                            return 1;
                        }
                    }

                    UUID sessionId1 = t1.getSession() != null ? t1.getSession().getSessionId() : null;
                    UUID sessionId2 = t2.getSession() != null ? t2.getSession().getSessionId() : null;

                    if (sessionId1 != null && sessionId2 != null) {
                        int sessionCompare = sessionId1.compareTo(sessionId2);
                        if (sessionCompare != 0) {
                            return sessionCompare;
                        }
                    }

                    int rowCompare = Integer.compare(t1.getRow(), t2.getRow());
                    if (rowCompare != 0) {
                        return rowCompare;
                    }
                    return Integer.compare(t1.getSeat(), t2.getSeat());
                });
            }

            model.addAttribute("tickets", tickets);
            model.addAttribute("cinemaSessions", sessionRepository.findAll());
            model.addAttribute("selectedSessionId", sessionId);
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("discountTypes", DiscountType.values());
            model.addAttribute("commandHistory", commandManager.getCommandHistory());
            model.addAttribute("canUndo", commandManager.canUndo());

            return "ticket/listTickets";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке билетов: " + e.getMessage());
            return "ticket/listTickets";
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

            // Используем команду для покупки
            CommandResult result = commandManager.executePurchase(ticket, user, discountType);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("success", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", result.getMessage());
            }

            return "redirect:/tickets";

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

            // Используем команду для отмены
            CommandResult result = commandManager.executeCancel(ticket);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("success", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", result.getMessage());
            }

            return "redirect:/tickets";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при отмене билета: " + e.getMessage());
            return "redirect:/tickets";
        }
    }

    @PostMapping("/undo")
    public String undoLastCommand(RedirectAttributes redirectAttributes) {
        try {
            CommandResult result = commandManager.undo();

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("success", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", result.getMessage());
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при отмене операции: " + e.getMessage());
        }

        return "redirect:/tickets";
    }

    @PostMapping("/clear-history")
    public String clearCommandHistory(RedirectAttributes redirectAttributes) {
        try {
            commandManager.clearHistory();
            redirectAttributes.addFlashAttribute("success", "История операций очищена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при очистке истории: " + e.getMessage());
        }
        return "redirect:/tickets";
    }

    @GetMapping("/calculate-price")
    @ResponseBody
    public Map<String, Object> calculatePrice(@RequestParam BigDecimal basePrice,
                                              @RequestParam DiscountType discountType) {
        Map<String, Object> result = new HashMap<>();

        try {
            PricingStrategy strategy = createPricingStrategy(discountType);
            BigDecimal finalPrice = strategy.calculatePrice(basePrice);
            BigDecimal discountAmount = basePrice.subtract(finalPrice);

            result.put("success", true);
            result.put("basePrice", basePrice);
            result.put("finalPrice", finalPrice);
            result.put("discountAmount", discountAmount);
            result.put("discountPercent", discountAmount.divide(basePrice, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            result.put("ticketTypeName", strategy.getStrategyName());

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    private PricingStrategy createPricingStrategy(DiscountType discountType) {
        switch (discountType) {
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
}