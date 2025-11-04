package com.example.cinema.cinema_app.film;

import com.example.cinema.cinema_app.genre.Genre;
import jakarta.persistence.*;
import java.util.List;

@Entity
@IdClass(FilmsGenresId.class)
@Table(name = "films_genres")
public class FilmsGenres {
    @Id
    @JoinColumn(name = "film_id", nullable = false)
    private Long filmId;

    @Id
    @JoinColumn(name = "genre_id", nullable = false)
    private Long genreId;

    @ManyToOne
    @MapsId("filmId")
    @JoinColumn(name = "film_id", referencedColumnName = "filmId", insertable = false, updatable = false)
    private Film film;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id", referencedColumnName = "genreId", insertable = false, updatable = false)
    private Genre genre;

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setGenres(List<Genre> genres) {
        if (genres != null) {
            for (Genre genre : genres) {
                setGenre(genre);
            }
        }
    }
    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }
}


