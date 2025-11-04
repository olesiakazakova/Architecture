package com.example.cinema.cinema_app.film;

import com.example.cinema.cinema_app.director.Director;
import jakarta.persistence.*;

@Entity
@Table(name = "films_directors")
public class FilmsDirectors {
    @EmbeddedId
    private FilmsDirectorsId id;

    @ManyToOne
    @MapsId("directorId")
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}


