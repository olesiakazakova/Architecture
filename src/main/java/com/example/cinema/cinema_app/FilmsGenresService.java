package com.example.cinema.cinema_app;

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
}
