package com.example.cinema.cinema_app.film.repository;

import com.example.cinema.cinema_app.film.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
}



