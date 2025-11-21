package com.example.cinema.cinema_app.film.service;

import com.example.cinema.cinema_app.film.FilmsActors;
import com.example.cinema.cinema_app.film.FilmsActorsId;
import com.example.cinema.cinema_app.film.repository.FilmsActorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FilmsActorsService {

    @Autowired
    private FilmsActorsRepository filmsActorsRepository;

    public List<FilmsActors> getAllFilmsActors() {
        return filmsActorsRepository.findAll();
    }

    public Optional<FilmsActors> getFilmsActorsById(Long actorId, Long filmId) {
        return filmsActorsRepository.findById(new FilmsActorsId(actorId, filmId));
    }

    public FilmsActors saveFilmsActors(FilmsActors filmsActors) {
        return filmsActorsRepository.save(filmsActors);
    }

    public void deleteFilmsActors(Long actorId, Long filmId) {
        filmsActorsRepository.deleteById(new FilmsActorsId(actorId, filmId));
    }
}



