package com.example.cinema.cinema_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    void deleteByEmail(String email);
}
