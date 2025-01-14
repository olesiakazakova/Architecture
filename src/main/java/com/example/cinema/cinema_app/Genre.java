package com.example.cinema.cinema_app;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "genres")
public class
Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "genre_seq", sequenceName = "genre_sequence", allocationSize = 1)
    private Long genreId;  // Это поле будет автоинкрементным

    @Column(nullable = false, unique = true)
    private String genre;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.PERSIST)
    private List<FilmsGenres> filmsGenres;

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<FilmsGenres> getFilmsGenres() {
        return filmsGenres;
    }

    public void setFilmsGenres(List<FilmsGenres> filmsGenres) {
        this.filmsGenres = filmsGenres;
    }
}


