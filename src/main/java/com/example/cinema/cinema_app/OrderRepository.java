package com.example.cinema.cinema_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderComposite, UUID> {

    List<OrderComposite> findByUser_Email(String email);

    List<OrderComposite> findByPurchasedFalse();

    List<OrderComposite> findAll();
}
