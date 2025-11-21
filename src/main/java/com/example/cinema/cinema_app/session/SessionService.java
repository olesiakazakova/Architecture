package com.example.cinema.cinema_app.session;

import com.example.cinema.cinema_app.session.state.SessionStatus;
import com.example.cinema.cinema_app.ticket.DiscountType;
import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.ticket.TicketRepository;
import com.example.cinema.cinema_app.user.User;
import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.film.service.FilmService;
import com.example.cinema.cinema_app.hall.Hall;
import com.example.cinema.cinema_app.hall.HallRepository;
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

    public List<Session> findAll() {
        List<Session> sessions = sessionRepository.findAll();
        return sessions;
    }

    public Session findById(UUID id) {
        return sessionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Session save(Session session) {
        Session savedSession = sessionRepository.save(session);
        createTicketsForSession(savedSession);

        return savedSession;
    }

    @Transactional
    public Session update(UUID sessionId, Session sessionDetails) {
        return sessionRepository.findById(sessionId)
                .map(session -> {
                    Hall oldHall = session.getHall();

                    session.setFilm(sessionDetails.getFilm());
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

    public void createTicketsForSession(Session session) {
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