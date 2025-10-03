package com.example.cinema.cinema_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FilmsGenresRepository extends JpaRepository<FilmsGenres, Long> {
    Optional<FilmsGenres> findByGenreAndFilm(Genre genre, Film film);

    void deleteByGenre(Genre genre);
}
