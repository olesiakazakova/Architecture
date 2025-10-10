package com.example.cinema.cinema_app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
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
        List<Session> sessions = sessionService.findAll();
        Map<String, Object> flyweightStats = sessionService.getFlyweightStats();

        model.addAttribute("sessions", sessions);
        model.addAttribute("flyweightStats", flyweightStats);
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

            Map<String, Object> stats = sessionService.getFlyweightStats();
            redirectAttributes.addFlashAttribute("success",
                    "Сеанс успешно создан! Создано " + tickets.size() + " билетов. " +
                            "Flyweight cache: " + stats.get("cacheSize") + " films");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при создании сеанса: " + e.getMessage());
        }
        return "redirect:/sessions";
    }

    @GetMapping("/film/{filmId}")
    public String getSessionsByFilm(@PathVariable Long filmId, Model model) {
        List<Session> sessions = sessionService.findByFilmId(filmId);
        Film film = filmService.findById(filmId);

        model.addAttribute("sessions", sessions);
        model.addAttribute("film", film);
        model.addAttribute("flyweightInfo",
                "Используется один экземпляр фильма для " + sessions.size() + " сеансов");

        return "session/sessionsByFilm";
    }

    @GetMapping("/flyweight-stats")
    @ResponseBody
    public Map<String, Object> getFlyweightStats() {
        return sessionService.getFlyweightStats();
    }

    // Остальные методы контроллера без изменений...
    @GetMapping("/edit/{sessionId}")
    public String showEditForm(@PathVariable UUID sessionId, Model model) {
        Session session = sessionService.findById(sessionId);
        if (session == null) {
            return "redirect:/sessions";
        }
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

