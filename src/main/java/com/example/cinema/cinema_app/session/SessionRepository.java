package com.example.cinema.cinema_app.session;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    @Query("SELECT s FROM Session s JOIN FETCH s.film JOIN FETCH s.hall")
    List<Session> findAll();

}
