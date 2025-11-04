package com.example.cinema.cinema_app.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByFilm_FilmId(Long filmId);
    List<Session> findByHall_HallId(Integer hallId);
}
