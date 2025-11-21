package com.example.cinema.cinema_app.session;

import com.example.cinema.cinema_app.session.state.SessionStatus;
import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.film.service.FilmService;
import com.example.cinema.cinema_app.hall.HallService;
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
    private HallService hallService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private SessionService sessionService;

    @GetMapping()
    public String getAllSessions(Model model) {
        List<Session> sessions = sessionRepository.findAll();
        model.addAttribute("sessionItems", sessions);
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
            Session savedSession = sessionService.save(session);
            List<Ticket> tickets = sessionService.getTicketsForSession(savedSession.getSessionId());

        } catch (Exception e) {
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
        UUID sessionId = sessionDetails.getSessionId();
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

