package com.example.cinema.cinema_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmsActorsRepository extends JpaRepository<FilmsActors, FilmsActorsId> {
}
