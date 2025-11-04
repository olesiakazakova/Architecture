package com.example.cinema.cinema_app.film;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class FilmsActorsId implements Serializable {
    private Long actorId;
    private Long filmId;

    public FilmsActorsId() {}

    public FilmsActorsId(Long actor, Long film) {
        this.actorId = actor;
        this.filmId = film;
    }

    public Long getActor() {
        return actorId;
    }

    public void setActor(Long actor) {
        this.actorId = actor;
    }

    public Long getFilm() {
        return filmId;
    }

    public void setFilm(Long film) {
        this.filmId = film;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilmsActorsId)) return false;
        FilmsActorsId that = (FilmsActorsId) o;
        return Objects.equals(actorId, that.actorId) && Objects.equals(filmId, that.filmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actorId, filmId);
    }
}





