package com.example.cinema.cinema_app;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public String getAllTickets(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        tickets.forEach(ticket -> {
            System.out.println("Ticket ID: " + ticket.getTicketId());
            System.out.println("Session: " + ticket.getSession());
            System.out.println("User: " + ticket.getUser());
            if (ticket.getSession() != null) {
                System.out.println("Session price: " + ticket.getSession().getCost());
            }
        });
        List<Session> cinemaSessions = sessionRepository.findAll();
        model.addAttribute("cinemaSessions", cinemaSessions);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        List<String> formattedStartTimes = cinemaSessions.stream()
                .map(session -> {
                    return session.getStartTime() != null ? session.getStartTime().format(timeFormatter) : "Неизвестно";
                })
                .collect(Collectors.toList());

        model.addAttribute("formattedStartTimes", formattedStartTimes);
        model.addAttribute("tickets", tickets);
        return "session/listTickets";
    }

    @GetMapping("/add")
    public String showAddTicketForm(Model model) {
        try {
            model.addAttribute("cinemaSessions", sessionRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("discountTypes", DiscountType.values());
            return "session/addTicket";
        } catch (Exception e) {
            return "redirect:/tickets?error=" + e.getMessage();
        }
    }

    @PostMapping("/add")
    public String addTicket(@RequestParam("sessionId") UUID sessionId,
                            @RequestParam("userEmail") String userEmail,
                            @RequestParam("row") int row,
                            @RequestParam("seat") int seat,
                            @RequestParam("discountType") DiscountType discountType) {

        try {
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found"));
            User user = userRepository.findById(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Ticket ticket = new Ticket();
            ticket.setSession(session);
            ticket.setUser(user);
            ticket.setRow(row);
            ticket.setSeat(seat);
            ticket.setDiscount(discountType);
            ticket.setTicketTypeFactory(ticketTypeFactory);

            ticketRepository.save(ticket);
            return "redirect:/tickets";

        } catch (Exception e) {
            return "redirect:/tickets/add?error=" + e.getMessage();
        }
    }

    @GetMapping("/edit")
    public String showEditTicketForm(@RequestParam("ticketId") UUID ticketId, Model model) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

            model.addAttribute("ticket", ticket);
            model.addAttribute("cinemaSessions", sessionRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("discountTypes", DiscountType.values());

            return "session/editTicket";
        } catch (Exception e) {
            return "redirect:/tickets?error=" + e.getMessage();
        }
    }

    @PostMapping("/edit")
    public String editTicket(@RequestParam("ticketId") UUID ticketId,
                             @RequestParam("sessionId") UUID sessionId,
                             @RequestParam("userEmail") String userEmail,
                             @RequestParam("row") int row,
                             @RequestParam("seat") int seat,
                             @RequestParam("discountType") DiscountType discountType) {

        try {
            Ticket ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found"));
            User user = userRepository.findById(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            ticket.setSession(session);
            ticket.setUser(user);
            ticket.setRow(row);
            ticket.setSeat(seat);
            ticket.setDiscount(discountType);
            ticket.setTicketTypeFactory(ticketTypeFactory);

            ticketRepository.save(ticket);
            return "redirect:/tickets";

        } catch (Exception e) {
            return "redirect:/tickets/edit?ticketId=" + ticketId + "&error=" + e.getMessage();
        }
    }

    @PostMapping("/delete")
    public String deleteTicket(@RequestParam UUID ticketId) {
        try {
            ticketRepository.deleteById(ticketId);
            return "redirect:/tickets";
        } catch (Exception e) {
            return "redirect:/tickets?error=Ticket not found";
        }
    }
    // Вспомогательный метод для конвертации строки в DiscountType
    private DiscountType mapTicketTypeToDiscountType(String ticketType) {
        if (ticketType == null) {
            return DiscountType.NO_DISCOUNT;
        }

        switch (ticketType.toLowerCase()) {
            case "student": return DiscountType.STUDENT_DISCOUNT;
            case "child": return DiscountType.CHILD_DISCOUNT;
            case "senior": return DiscountType.SENIOR_DISCOUNT;
            default: return DiscountType.NO_DISCOUNT;
        }
    }
}

