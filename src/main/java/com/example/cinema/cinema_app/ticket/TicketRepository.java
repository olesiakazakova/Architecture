package com.example.cinema.cinema_app.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByIsPurchasedFalse();

    List<Ticket> findBySession_SessionId(UUID sessionId);

    List<Ticket> findBySession_SessionIdOrderByRowAscSeatAsc(UUID sessionId);

    List<Ticket> findBySession_SessionIdAndIsPurchased(UUID sessionId, boolean isPurchased);

    List<Ticket> findBySession_SessionIdAndIsPurchasedFalse(UUID sessionId);

    Optional<Ticket> findBySession_SessionIdAndRowAndSeatAndIsPurchasedTrue(UUID sessionId, int row, int seat);

    Optional<Ticket> findBySession_SessionIdAndRowAndSeat(UUID sessionId, int row, int seat);

    int countBySession_SessionId(UUID sessionId);

    List<Ticket> findByIsPurchasedTrue();
}
