package com.example.cinema.cinema_app;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private FilmFlyweightFactory filmFactory;

    @Autowired
    private FilmService filmService;

    @PostConstruct
    public void init() {
        // Предзагрузка всех фильмов при старте приложения
        preloadAllFilms();
    }

    private void preloadAllFilms() {
        List<Film> allFilms = filmService.findAll();
        filmFactory.preloadFilms(allFilms);
        System.out.println("Preloaded " + allFilms.size() + " films into Flyweight cache");
    }

    public List<Session> findAll() {
        List<Session> sessions = sessionRepository.findAll();

        // Инициализируем Flyweight для всех сеансов
        sessions.forEach(session -> session.initFlyweight(filmFactory, filmService));

        return sessions;
    }

    public Session findById(UUID id) {
        Session session = sessionRepository.findById(id).orElse(null);
        if (session != null) {
            session.initFlyweight(filmFactory, filmService);
        }
        return session;
    }

    @Transactional
    public Session save(Session session) {
        // Инициализируем Flyweight перед сохранением
        session.initFlyweight(filmFactory, filmService);

        // Убеждаемся, что фильм находится в кэше
        if (session.getFilm() != null) {
            filmFactory.putFilm(session.getFilm());
        }

        Session savedSession = sessionRepository.save(session);
        createTicketsForSession(savedSession);

        return savedSession;
    }


    @Transactional
    public Session update(UUID sessionId, Session sessionDetails) {
        return sessionRepository.findById(sessionId)
                .map(session -> {
                    Hall oldHall = session.getHall();

                    // Обновляем фильм и кэшируем его
                    session.setFilm(sessionDetails.getFilm());
                    filmFactory.putFilm(sessionDetails.getFilm());

                    session.setHall(sessionDetails.getHall());
                    session.setDate(sessionDetails.getDate());
                    session.setStartTime(sessionDetails.getStartTime());
                    session.setCost(sessionDetails.getCost());

                    Session updatedSession = sessionRepository.save(session);

                    if (oldHall.getHallId() != sessionDetails.getHall().getHallId()) {
                        recreateTicketsForSession(updatedSession);
                    }

                    return updatedSession;
                })
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));
    }

    public void delete(UUID id) {
        sessionRepository.deleteById(id);
    }

    // Метод для массового создания сеансов с оптимизацией Flyweight
    @Transactional
    public List<Session> createSessionsForFilm(Film film, List<Session> sessionTemplates) {
        // Кэшируем фильм один раз
        filmFactory.putFilm(film);

        List<Session> savedSessions = new ArrayList<>();

        for (Session template : sessionTemplates) {
            template.setFilm(film);
            template.initFlyweight(filmFactory, filmService);

            Session savedSession = sessionRepository.save(template);
            createTicketsForSession(savedSession);
            savedSessions.add(savedSession);
        }

        System.out.println("Created " + savedSessions.size() + " sessions for film: " + film.getName());
        System.out.println("Flyweight cache size: " + filmFactory.getCacheSize());

        return savedSessions;
    }

    // Метод для получения сеансов по фильму с Flyweight оптимизацией
    public List<Session> findByFilmId(Long filmId) {
        // Загружаем фильм через Flyweight
        Film film = filmFactory.getOrLoadFilm(filmId, filmService);

        List<Session> sessions = sessionRepository.findByFilm_FilmId(filmId);

        // Устанавливаем общий фильм для всех сеансов
        sessions.forEach(session -> {
            session.setFilm(film); // Используем один экземпляр
            session.initFlyweight(filmFactory, filmService);
        });

        return sessions;
    }

    // Метод для получения статистики по использованию Flyweight
    public Map<String, Object> getFlyweightStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", filmFactory.getCacheSize());

        List<Session> allSessions = sessionRepository.findAll();
        long uniqueFilms = allSessions.stream()
                .map(Session::getFilmId)
                .distinct()
                .count();

        stats.put("totalSessions", allSessions.size());
        stats.put("uniqueFilms", uniqueFilms);
        stats.put("memorySavings", calculateMemorySavings(allSessions.size(), (int) uniqueFilms));

        return stats;
    }

    private String calculateMemorySavings(int totalSessions, int uniqueFilms) {
        // Примерная оценка экономии памяти
        int estimatedFilmSize = 500; // bytes per Film object
        int withoutFlyweight = totalSessions * estimatedFilmSize;
        int withFlyweight = uniqueFilms * estimatedFilmSize;
        int savings = withoutFlyweight - withFlyweight;

        return String.format("Saved approximately %d KB memory", savings / 1024);
    }


    private void createTicketsForSession(Session session) {
        try {
            Hall hall = session.getHall();
            List<Ticket> tickets = new ArrayList<>();

            System.out.println("Creating tickets for session: " + session.getSessionId());
            System.out.println("Hall: " + hall.getHallId() + ", Seats: " + hall.getNumberSeats());

            Ticket prototype = new Ticket();
            prototype.setSession(session);
            prototype.setDiscount(DiscountType.NO_DISCOUNT);
            prototype.setIsPurchased(false);

            SeatingArrangement arrangement = calculateSeatingArrangement(hall.getNumberSeats());

            System.out.println("Rows: " + arrangement.getRows() + ", Base seats per row: " + arrangement.baseSeatsPerRow);

            for (int row = 1; row <= arrangement.getRows(); row++) {
                int seatsInThisRow = arrangement.getSeatsInRow(row);
                for (int seat = 1; seat <= seatsInThisRow; seat++) {
                    Ticket ticket = prototype.copy();
                    ticket.setRow(row);
                    ticket.setSeat(seat);
                    tickets.add(ticket);
                    System.out.println("Created ticket: Row " + row + ", Seat " + seat);
                }
            }

            ticketRepository.saveAll(tickets);
            System.out.println("Created " + tickets.size() + " tickets for session " + session.getSessionId());

        } catch (Exception e) {
            System.err.println("Error creating tickets for session: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void recreateTicketsForSession(Session session) {
        List<Ticket> existingTickets = ticketRepository.findBySession_SessionId(session.getSessionId());
        if (!existingTickets.isEmpty()) {
            ticketRepository.deleteAll(existingTickets);
        }

        createTicketsForSession(session);
    }

    private SeatingArrangement calculateSeatingArrangement(int totalSeats) {
        int baseRows;
        int baseSeatsPerRow;

        if (totalSeats <= 20) {
            baseRows = 4;
            baseSeatsPerRow = totalSeats / 4;
        } else if (totalSeats <= 50) {
            baseRows = 6;
            baseSeatsPerRow = totalSeats / 6;
        } else {
            baseRows = 8;
            baseSeatsPerRow = totalSeats / 8;
        }

        int remainingSeats = totalSeats % baseRows;

        return new SeatingArrangement(baseRows, baseSeatsPerRow, remainingSeats);
    }

    public List<Ticket> getTicketsForSession(UUID sessionId) {
        return ticketRepository.findBySession_SessionIdOrderByRowAscSeatAsc(sessionId);
    }

    public Map<String, Object> getSessionStats(UUID sessionId) {
        List<Ticket> tickets = ticketRepository.findBySession_SessionId(sessionId);
        int totalSeats = tickets.size();
        int purchasedTickets = (int) tickets.stream().filter(ticket -> ticket.getIsPurchased()).count();
        int availableSeats = totalSeats - purchasedTickets;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSeats", totalSeats);
        stats.put("purchasedTickets", purchasedTickets);
        stats.put("availableSeats", availableSeats);
        stats.put("occupancyRate", totalSeats > 0 ? (double) purchasedTickets / totalSeats * 100 : 0);

        return stats;
    }

    public Map<Integer, List<Ticket>> getHallSeatingChart(UUID sessionId) {
        List<Ticket> tickets = ticketRepository.findBySession_SessionIdOrderByRowAscSeatAsc(sessionId);
        return tickets.stream().collect(Collectors.groupingBy(Ticket::getRow));
    }

    @Transactional
    public void createTicketsForExistingSessions() {
        List<Session> sessions = sessionRepository.findAll();
        for (Session session : sessions) {
            List<Ticket> existingTickets = ticketRepository.findBySession_SessionId(session.getSessionId());
            if (existingTickets.isEmpty()) {
                System.out.println("Creating tickets for existing session: " + session.getSessionId());
                createTicketsForSession(session);
            }
        }
    }

    private static class SeatingArrangement {
        private final int baseRows;
        private final int baseSeatsPerRow;
        private final int remainingSeats;

        public SeatingArrangement(int baseRows, int baseSeatsPerRow, int remainingSeats) {
            this.baseRows = baseRows;
            this.baseSeatsPerRow = baseSeatsPerRow;
            this.remainingSeats = remainingSeats;
        }

        public int getRows() {
            return baseRows + (remainingSeats > 0 ? 1 : 0);
        }

        public int getSeatsInRow(int row) {
            if (row <= baseRows) {
                return baseSeatsPerRow;
            } else {
                return remainingSeats;
            }
        }
    }
}