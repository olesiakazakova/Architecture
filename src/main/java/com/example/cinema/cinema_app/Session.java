package com.example.cinema.cinema_app;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private FilmFlyweightFactory filmFactory;

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

    // Метод для инициализации Flyweight зависимостей
    public void initFlyweight(FilmFlyweightFactory factory, FilmService service) {
        this.filmFactory = factory;
        this.filmService = service;

        // Кэшируем фильм при инициализации
        if (this.film != null && this.film.getFilmId() != null) {
            factory.putFilm(this.film);
        }
    }

    // Геттер, который использует Flyweight при наличии фабрики
    public Film getFilm() {
        if (filmFactory != null && film != null && film.getFilmId() != null) {
            Film cachedFilm = filmFactory.getFilm(film.getFilmId());
            if (cachedFilm != null) {
                return cachedFilm;
            } else {
                // Если фильма нет в кэше, добавляем его
                filmFactory.putFilm(film);
            }
        }
        return film;
    }

    // Сеттер, который обновляет кэш
    public void setFilm(Film film) {
        this.film = film;
        if (filmFactory != null && film != null && film.getFilmId() != null) {
            filmFactory.putFilm(film);
        }
    }

    // Геттер для прямого доступа к filmId (полезно для оптимизации)
    public Long getFilmId() {
        return film != null ? film.getFilmId() : null;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
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