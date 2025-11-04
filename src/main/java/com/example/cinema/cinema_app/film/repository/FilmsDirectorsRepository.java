package com.example.cinema.cinema_app.film.repository;

import com.example.cinema.cinema_app.film.FilmsDirectors;
import com.example.cinema.cinema_app.film.FilmsDirectorsId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FilmsDirectorsRepository extends JpaRepository<FilmsDirectors, FilmsDirectorsId> {
    Optional<FilmsDirectors> findByDirector_DirectorIdAndFilm_FilmId(Long directorId, Long filmId);
}







