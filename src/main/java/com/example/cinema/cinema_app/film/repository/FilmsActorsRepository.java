package com.example.cinema.cinema_app.film.repository;

import com.example.cinema.cinema_app.film.FilmsActors;
import com.example.cinema.cinema_app.film.FilmsActorsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmsActorsRepository extends JpaRepository<FilmsActors, FilmsActorsId> {
}
