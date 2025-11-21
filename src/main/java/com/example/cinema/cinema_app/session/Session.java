package com.example.cinema.cinema_app.session;

import com.example.cinema.cinema_app.session.state.*;
import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.film.service.FilmService;
import com.example.cinema.cinema_app.hall.Hall;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue
    private UUID sessionId;

    @ManyToOne
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "date", nullable = false)
    private java.sql.Date date;

    @Column(name = "start_time", nullable = false)
    private java.time.LocalTime startTime;

    @Column(name = "cost", nullable = false)
    private java.math.BigDecimal cost;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @Transient
    private FilmService filmService;

    public Session() {
    }

    public Session(Film film, Hall hall, java.sql.Date date, java.time.LocalTime startTime, java.math.BigDecimal cost) {
        this.film = film;
        this.hall = hall;
        this.date = date;
        this.startTime = startTime;
        this.cost = cost;
    }

    public SessionStatus getSessionState() {
        if (film == null || date == null || startTime == null) {
            return SessionStatus.SCHEDULED;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sessionDateTime = LocalDateTime.of(date.toLocalDate(), startTime);
        LocalDateTime sessionEndTime = getEndDateTime();


        // Корректировка для ночных сеансов (начало до 06:00)
        if (startTime.isBefore(LocalTime.of(6, 0))) {
            sessionDateTime = sessionDateTime.plusDays(1);
            if (sessionEndTime != null) {
                sessionEndTime = sessionEndTime.plusDays(1);
            }
        }

        // Если длительность не известна, используем запасное значение
        if (sessionEndTime == null) {
            sessionEndTime = sessionDateTime.plusMinutes(1);
        }

        // Определяем статус
        if (now.isBefore(sessionDateTime)) {
            return SessionStatus.SCHEDULED;
        } else if (now.isBefore(sessionEndTime) || now.equals(sessionDateTime)) {
            return SessionStatus.ACTIVE;
        } else {
            return SessionStatus.COMPLETED;
        }
    }


    // Создаем состояние
    private SessionState createSessionState() {
        SessionStatus status = getSessionState();
        switch (status) {
            case SCHEDULED:
                return new ScheduledSessionState();
            case ACTIVE:
                return new ActiveSessionState();
            case COMPLETED:
                return new CompletedSessionState();
            default:
                return new ScheduledSessionState();
        }
    }

    public boolean canPurchaseTickets() {
        SessionState state = createSessionState();
        return state.canPurchaseTickets();
    }

    public boolean canCancelTickets() {
        SessionState state = createSessionState();
        return state.canCancelTickets();
    }

    public boolean canModifySession() {
        SessionState state = createSessionState();
        return state.canModifySession();
    }

    public String getStatusMessage() {
        SessionState state = createSessionState();
        return state.getStatusMessage();
    }

    public LocalDateTime getEndDateTime() {
        if (film == null || date == null || startTime == null) {
            return null;
        }

        try {
            return LocalDateTime.of(date.toLocalDate(), startTime)
                    .plusMinutes(film.getDuration());
        } catch (Exception e) {
            return null;
        }
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public java.time.LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(java.time.LocalTime startTime) {
        this.startTime = startTime;
    }

    public java.math.BigDecimal getCost() {
        return cost;
    }

    public void setCost(java.math.BigDecimal cost) {
        this.cost = cost;
    }


}