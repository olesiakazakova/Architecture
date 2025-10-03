package com.example.cinema.cinema_app;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FilmsDirectorsId implements Serializable {
    private Long directorId;
    private Long filmId;

    public FilmsDirectorsId() {}

    public FilmsDirectorsId(Long directorId, Long filmId) {
        this.directorId = directorId;
        this.filmId = filmId;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilmsDirectorsId)) return false;
        FilmsDirectorsId that = (FilmsDirectorsId) o;
        return Objects.equals(directorId, that.directorId) &&
                Objects.equals(filmId, that.filmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directorId, filmId);
    }
}


