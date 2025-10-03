package com.example.cinema.cinema_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FilmsDirectorsService {

    @Autowired
    private FilmsDirectorsRepository filmsDirectorsRepository;

    public List<FilmsDirectors> getAllFilmsDirectors() {
        return filmsDirectorsRepository.findAll();
    }

    public Optional<FilmsDirectors> getFilmsDirectorsById(Long directorId, Long filmId) {
        return filmsDirectorsRepository.findByDirector_DirectorIdAndFilm_FilmId(directorId, filmId);
    }

    public FilmsDirectors saveFilmsDirectors(FilmsDirectors filmsDirectors) {
        return filmsDirectorsRepository.save(filmsDirectors);
    }

    public boolean deleteFilmsDirectors(Long directorId, Long filmId) {
        Optional<FilmsDirectors> filmsDirectors = filmsDirectorsRepository.findByDirector_DirectorIdAndFilm_FilmId(directorId, filmId);
        if (filmsDirectors.isPresent()) {
            filmsDirectorsRepository.delete(filmsDirectors.get());
            return true;
        }
        return false;
    }
}


