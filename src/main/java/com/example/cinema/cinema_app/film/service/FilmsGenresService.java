package com.example.cinema.cinema_app.film.service;

import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.film.FilmsGenres;
import com.example.cinema.cinema_app.film.repository.FilmsGenresRepository;
import com.example.cinema.cinema_app.genre.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class FilmsGenresService {
    @Autowired
    private FilmsGenresRepository filmsGenresRepository;

    public FilmsGenres createFilmsGenres(FilmsGenres filmsGenres) {
        return filmsGenresRepository.save(filmsGenres);
    }

    public boolean deleteFilmsGenres(Genre genre, Film film) {
        Optional<FilmsGenres> filmsGenres = filmsGenresRepository.findByGenreAndFilm(genre, film);
        if (filmsGenres.isPresent()) {
            filmsGenresRepository.delete(filmsGenres.get());
            return true;
        }
        return false;
    }

    public void deleteAllByGenre(Genre genre) {
        filmsGenresRepository.deleteByGenre(genre);
    }
}
