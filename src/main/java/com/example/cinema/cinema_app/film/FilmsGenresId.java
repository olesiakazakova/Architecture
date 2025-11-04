package com.example.cinema.cinema_app.film;

import java.io.Serializable;
import java.util.Objects;

public class FilmsGenresId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long filmId;
    private Long genreId;

    public FilmsGenresId() {}

    public FilmsGenresId(Long filmId, Long genreId) {
        this.filmId = filmId;
        this.genreId = genreId;
    }

    public Long getFilmId() {
        return filmId;
    }

    public void setFilmId(Long filmId) {
        this.filmId = filmId;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilmsGenresId)) return false;
        FilmsGenresId that = (FilmsGenresId) o;
        return Objects.equals(filmId, that.filmId) && Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filmId, genreId);
    }
}


