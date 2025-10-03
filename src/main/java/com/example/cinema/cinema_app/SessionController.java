package com.example.cinema.cinema_app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionRepository filmRepository;

    @Autowired
    private SessionRepository hallRepository;

    @Autowired
    private HallService hallService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private SessionService sessionService;


    @GetMapping()
    public String getAllSessions(Model model) {
        List<Session> sessions = sessionRepository.findAll();
        model.addAttribute("sessions", sessions);
        return "session/listSessions";
    }

    @GetMapping("/add")
    public String createSessionForm(Model model) {
        model.addAttribute("session", new Session());
        model.addAttribute("films", filmService.findAll());
        model.addAttribute("halls", hallService.findAll());
        return "session/addSession";
    }

    @PostMapping("/add")
    public String createSession(@ModelAttribute Session session, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Creating new session with hall: " + session.getHall().getHallId());
            System.out.println("Hall seats: " + session.getHall().getNumberSeats());

            Session savedSession = sessionService.save(session);

            List<Ticket> tickets = sessionService.getTicketsForSession(savedSession.getSessionId());
            System.out.println("Tickets created: " + tickets.size());

            redirectAttributes.addFlashAttribute("success",
                    "Сеанс успешно создан! Создано " + tickets.size() + " билетов.");
        } catch (Exception e) {
            System.err.println("Error creating session: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при создании сеанса: " + e.getMessage());
        }
        return "redirect:/sessions";
    }

    @GetMapping("/edit/{sessionId}")
    public String showEditForm(@PathVariable UUID sessionId, Model model) {
        Session session = sessionService.findById(sessionId);
        System.out.println("Session ID: " + sessionService.findById(sessionId));
        System.out.println("Session ID: " + session.getSessionId());
        if (session == null) {
            return "redirect:/sessions";    }
        model.addAttribute("session", session);
        model.addAttribute("films", filmService.findAll());
        model.addAttribute("halls", hallService.findAll());
        return "session/editSession";
    }

    @PostMapping("/update")
    public String updateSession(@ModelAttribute Session sessionDetails,
                                RedirectAttributes redirectAttributes) {
        UUID sessionId = sessionDetails.getSessionId(); // Получаем идентификатор из объекта
        return sessionRepository.findById(sessionId)
                .map(session -> {
                    session.setFilm(sessionDetails.getFilm());
                    session.setHall(sessionDetails.getHall());
                    session.setDate(sessionDetails.getDate());
                    session.setStartTime(sessionDetails.getStartTime());
                    session.setCost(sessionDetails.getCost());
                    sessionRepository.save(session);
                    redirectAttributes.addFlashAttribute("success", "Сеанс успешно обновлен!");
                    return "redirect:/sessions";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Сеанс не найден.");
                    return "redirect:/sessions";
                });
    }

    @PostMapping("/delete/{id}")
    public String deleteSession(@PathVariable UUID id) {
        try {
            sessionRepository.deleteById(id);
            return "redirect:/sessions";
        } catch (Exception e) {
            return "film/error";
        }
    }
}



