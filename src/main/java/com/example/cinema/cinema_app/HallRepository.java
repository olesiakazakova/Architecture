package com.example.cinema.cinema_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallRepository extends JpaRepository<Hall, Integer> {
    List<Hall> findByHallType(String hallType);
    List<Hall> findByHas3dTrue();
    List<Hall> findByHasDolbyTrue();
    List<Hall> findByScreenSizeGreaterThan(double screenSize);
}