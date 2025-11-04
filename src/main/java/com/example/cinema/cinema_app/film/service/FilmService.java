package com.example.cinema.cinema_app.film.service;

import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.film.FilmFlyweightFactory;
import com.example.cinema.cinema_app.film.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FilmService {
    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmFlyweightFactory filmFactory;

    public List<Film> findAll() {
        List<Film> films = filmRepository.findAll();
        // Автоматически кэшируем все загруженные фильмы
        filmFactory.preloadFilms(films);
        return films;
    }

    public Film findById(Long id) {
        Film film = filmRepository.findById(id).orElse(null);
        if (film != null) {
            filmFactory.putFilm(film);
        }
        return film;
    }

    public Film save(Film film) {
        Film savedFilm = filmRepository.save(film);
        filmFactory.putFilm(savedFilm);
        return savedFilm;
    }

    public void delete(Long id) {
        filmRepository.deleteById(id);
        filmFactory.removeFilm(id);
    }
}