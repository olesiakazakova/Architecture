package com.example.cinema.cinema_app.tests;

import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.hall.Hall;
import com.example.cinema.cinema_app.session.Session;
import com.example.cinema.cinema_app.session.SessionRepository;
import com.example.cinema.cinema_app.session.SessionService;
import com.example.cinema.cinema_app.session.state.SessionStatus;
import com.example.cinema.cinema_app.ticket.DiscountType;
import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.ticket.TicketRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// --- ТЕСТИРОВАНИЕ ДЛЯ Session ---

@ExtendWith(MockitoExtension.class)
public class SessionUnitTest {

    @Mock
    private Film film;
    @Mock
    private Hall hall;

    private Session session;
    private LocalDate today;
    private LocalTime now;
    private java.sql.Date sessionDate;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        now = LocalTime.now();
        sessionDate = java.sql.Date.valueOf(today);

        session = new Session(film, hall, sessionDate, now, BigDecimal.TEN);
    }

    // --- ТЕСТЫ ДЛЯ getSessionState ---

    @Test
    void testGetSessionState_Scheduled_WhenFuture() {
        LocalTime futureTime = now.plusHours(1);
        session.setStartTime(futureTime);

        assertEquals(SessionStatus.SCHEDULED, session.getSessionState());
    }

    @Test
    void testGetSessionState_Completed_WhenFinished() {
        LocalTime pastTime = now.minusHours(3);
        session.setStartTime(pastTime);

        assertEquals(SessionStatus.COMPLETED, session.getSessionState());
    }

    // тек вр = начало сеанса
    @Test
    void testGetSessionState_ExactlyAtStart_ShouldBeActive() {
        when(film.getDuration()).thenReturn(120);
        session.setStartTime(now);

        assertEquals(SessionStatus.ACTIVE, session.getSessionState());
    }


    // тек вр = конец сеанса
    @Test
    void testGetSessionState_ExactlyAtEnd_ShouldBeCompleted() {
        LocalTime endTime = now.minusMinutes(120);
        session.setStartTime(endTime);

        assertEquals(SessionStatus.COMPLETED, session.getSessionState());
    }

    // --- ТЕСТЫ ДЛЯ getEndTime

    @Test
    void testGetEndDateTime_NullFilm_ReturnsNull() {
        session.setFilm(null);

        assertNull(session.getEndDateTime());
    }

    @Test
    void testGetEndDateTime_NullDate_ReturnsNull() {
        session.setDate(null);

        assertNull(session.getEndDateTime());
    }

    @Test
    void testGetEndDateTime_NullStartTime_ReturnsNull() {
        session.setStartTime(null);

        assertNull(session.getEndDateTime());
    }

    @Test
    void testGetEndDateTime_CorrectCalculation() {
        when(film.getDuration()).thenReturn(120);

        LocalDateTime endDateTime = session.getEndDateTime();
        LocalDateTime expected = LocalDateTime.of(today, now).plusMinutes(120);

        assertEquals(expected, endDateTime);
    }

    // --- ТЕСТЫ ДЛЯ canPurchaseTickets ---

    @Test
    void testCanPurchaseTickets_WhenSessionIsScheduled_ShouldReturnTrue() {
        LocalTime futureTime = now.plusHours(2);
        session.setStartTime(futureTime);
        boolean result = session.canPurchaseTickets();

        assertTrue(result);
    }

    @Test
    void testCanPurchaseTickets_WhenSessionIsActive_ShouldReturnFalse() {
        LocalTime startTime = now.minusMinutes(10);
        session.setStartTime(startTime);
        boolean result = session.canPurchaseTickets();

        assertFalse(result);
    }

    @Test
    void testCanPurchaseTickets_WhenSessionIsCompleted_ShouldReturnFalse() {
        LocalTime pastTime = now.minusHours(3);
        session.setStartTime(pastTime);
        boolean result = session.canPurchaseTickets();

        assertFalse(result);
    }

    // --- ТЕСТЫ ДЛЯ canCancelTickets ---

    @Test
    void testCanCancelTickets_WhenSessionIsScheduled_ShouldReturnTrue() {
        LocalTime futureTime = now.plusHours(2);
        session.setStartTime(futureTime);
        boolean result = session.canCancelTickets();
        assertTrue(result);
    }

    @Test
    void tetCanCancelTickets_WhenSessionIsActive_ShouldReturnFalse() {
        LocalTime startTime = now.minusMinutes(10);
        session.setStartTime(startTime);
        boolean result = session.canCancelTickets();
        assertFalse(result);
    }

    @Test
    void testCanCancelTickets_WhenSessionIsCompleted_ShouldReturnFalse() {
        LocalTime pastTime = now.minusHours(3);
        session.setStartTime(pastTime);
        boolean result = session.canCancelTickets();
        assertFalse(result);
    }

    // --- ТЕСТЫ ДЛЯ canModifySession ---

    @Test
    void testCanModifySession_WhenSessionIsScheduled_ShouldReturnTrue() {
        LocalTime futureTime = now.plusHours(2);
        session.setStartTime(futureTime);
        boolean result = session.canModifySession();
        assertTrue(result);
    }

    @Test
    void testCanModifySession_WhenSessionIsActive_ShouldReturnFalse() {
        LocalTime startTime = now.minusMinutes(10);
        session.setStartTime(startTime);
        boolean result = session.canModifySession();
        assertFalse(result);
    }

    @Test
    void testCanModifySession_WhenSessionIsCompleted_ShouldReturnFalse() {
        LocalTime pastTime = now.minusHours(3);
        session.setStartTime(pastTime);
        boolean result = session.canModifySession();
        assertFalse(result);
    }
}

// --- ТЕСТИРОВАНИЕ ДЛЯ SessionService ---

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private SessionService sessionService;

    private UUID sessionId;
    private Film film;
    private Hall hall1, hall2;
    private Session existingSession;
    private Session updateData;
    private Session session;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();

        film = new Film();
        film.setFilmId(1L);
        film.setName("Test Film");
        film.setDuration(120);

        hall1 = new Hall();
        hall1.setHallId(1);
        hall1.setNumberSeats(50);

        hall2 = new Hall();
        hall2.setHallId(2);
        hall2.setNumberSeats(100);

        session = new Session();
        session.setSessionId(sessionId);
        session.setHall(hall1);

        existingSession = new Session(
                film,
                hall1,
                java.sql.Date.valueOf(LocalDate.now().plusDays(1)),
                LocalTime.of(18, 0),
                new BigDecimal("15.00")
        );
        existingSession.setSessionId(sessionId);

        updateData = new Session(
                film,
                hall2,
                java.sql.Date.valueOf(LocalDate.now().plusDays(2)),
                LocalTime.of(20, 0),
                new BigDecimal("20.00")
        );
    }

    // --- ТЕСТЫ ДЛЯ update ---

    // смена зала
    @Test
    void testUpdate_WhenSessionExistsAndHallChanged_ShouldUpdateSessionAndRecreateTickets() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
        when(sessionRepository.save(existingSession)).thenReturn(existingSession);
        when(ticketRepository.findBySession_SessionId(sessionId)).thenReturn(Collections.emptyList());
        when(ticketRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        Session result = sessionService.update(sessionId, updateData);

        assertNotNull(result);
        assertEquals(existingSession, result);
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).save(existingSession);
        verify(ticketRepository).findBySession_SessionId(sessionId);
        verify(ticketRepository).saveAll(anyList());
    }

    // ошибка, сеанса не найден
    @Test
    void update_WhenSessionNotFound_ShouldThrowIllegalArgumentException() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sessionService.update(sessionId, updateData);
        });

        assertEquals("Session not found with id: " + sessionId, exception.getMessage());
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository, never()).save(any());
        verify(ticketRepository, never()).findBySession_SessionId(any());
    }

    // обновление поля film
    @Test
    void update_WhenOnlyFilmChanged_ShouldUpdateCorrectly() {
        Film newFilm = new Film();
        newFilm.setFilmId(2L);
        newFilm.setName("New Film");
        updateData.setFilm(newFilm);
        updateData.setHall(hall1);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
        when(sessionRepository.save(existingSession)).thenReturn(existingSession);

        Session result = sessionService.update(sessionId, updateData);

        assertNotNull(result);
        assertEquals(newFilm, existingSession.getFilm());
        assertEquals(hall1, existingSession.getHall());
        verify(ticketRepository, never()).findBySession_SessionId(any());
    }

    // --- ТЕСТЫ ДЛЯ createTicketForSession

    // кол-во билетов = кол-во мест в зале
    @Test
    void createTicketsForSession_WithValidHall_ShouldCreateCorrectNumberOfTickets() {
        hall1.setNumberSeats(50);

        when(ticketRepository.saveAll(anyList())).thenReturn(List.of());

        sessionService.createTicketsForSession(session);

        ArgumentCaptor<List<Ticket>> ticketsCaptor = ArgumentCaptor.forClass(List.class);
        verify(ticketRepository).saveAll(ticketsCaptor.capture());
        List<Ticket> savedTickets = ticketsCaptor.getValue();

        assertNotNull(savedTickets);
        assertEquals(50, savedTickets.size());
    }

    // у созданных билетов корректно установлены поля
    @Test
    void createTicketsForSession_ShouldSetCorrectTicketProperties() {
        hall1.setNumberSeats(4);

        when(ticketRepository.saveAll(anyList())).thenReturn(List.of());

        sessionService.createTicketsForSession(session);

        ArgumentCaptor<List<Ticket>> ticketsCaptor = ArgumentCaptor.forClass(List.class);
        verify(ticketRepository).saveAll(ticketsCaptor.capture());

        List<Ticket> savedTickets = ticketsCaptor.getValue();
        Ticket firstTicket = savedTickets.get(0);

        assertEquals(session, firstTicket.getSession());
        assertEquals(DiscountType.NO_DISCOUNT, firstTicket.getDiscount());
        assertFalse(firstTicket.getIsPurchased());
        assertEquals(1, firstTicket.getRow());
        assertEquals(1, firstTicket.getSeat());
    }

    // раскладка мест по рядам небольшого зала
    @Test
    void createTicketsForSession_WithSmallHall_ShouldCreateCorrectArrangement() {
        hall1.setNumberSeats(16); // 4 ряда × 4 места

        when(ticketRepository.saveAll(anyList())).thenReturn(List.of());

        sessionService.createTicketsForSession(session);

        ArgumentCaptor<List<Ticket>> ticketsCaptor = ArgumentCaptor.forClass(List.class);
        verify(ticketRepository).saveAll(ticketsCaptor.capture());

        List<Ticket> savedTickets = ticketsCaptor.getValue();
        assertEquals(16, savedTickets.size());

        long row1Tickets = savedTickets.stream().filter(t -> t.getRow() == 1).count();
        long row2Tickets = savedTickets.stream().filter(t -> t.getRow() == 2).count();
        long row3Tickets = savedTickets.stream().filter(t -> t.getRow() == 3).count();
        long row4Tickets = savedTickets.stream().filter(t -> t.getRow() == 4).count();

        assertEquals(4, row1Tickets);
        assertEquals(4, row2Tickets);
        assertEquals(4, row3Tickets);
        assertEquals(4, row4Tickets);
    }

    // корректная генерация билетов для большого зала
    @Test
    void createTicketsForSession_WithLargeHall_ShouldCreateCorrectArrangement() {
        hall1.setNumberSeats(100);

        when(ticketRepository.saveAll(anyList())).thenReturn(List.of());

        sessionService.createTicketsForSession(session);

        ArgumentCaptor<List<Ticket>> ticketsCaptor = ArgumentCaptor.forClass(List.class);
        verify(ticketRepository).saveAll(ticketsCaptor.capture());

        List<Ticket> savedTickets = ticketsCaptor.getValue();
        assertEquals(100, savedTickets.size());
    }

    // неполный последний ряд
    @Test
    void createTicketsForSession_WithUnevenSeating_ShouldHandleRemainderCorrectly() {
        hall1.setNumberSeats(26); // 6 рядов × 4 места + 2 оставшихся

        when(ticketRepository.saveAll(anyList())).thenReturn(List.of());

        sessionService.createTicketsForSession(session);

        ArgumentCaptor<List<Ticket>> ticketsCaptor = ArgumentCaptor.forClass(List.class);
        verify(ticketRepository).saveAll(ticketsCaptor.capture());

        List<Ticket> savedTickets = ticketsCaptor.getValue();
        assertEquals(26, savedTickets.size());

        long lastRowTickets = savedTickets.stream()
                .filter(t -> t.getRow() == 7) // 6 базовых рядов + 1 для остатка
                .count();
        assertEquals(2, lastRowTickets);
    }

    // если 0 мест, не создает билеты
    @Test
    void createTicketsForSession_WithZeroSeats_ShouldHandleGracefully() {
        hall1.setNumberSeats(0);

        when(ticketRepository.saveAll(anyList())).thenReturn(List.of());

        sessionService.createTicketsForSession(session);

        ArgumentCaptor<List<Ticket>> ticketsCaptor = ArgumentCaptor.forClass(List.class);
        verify(ticketRepository).saveAll(ticketsCaptor.capture());

        List<Ticket> savedTickets = ticketsCaptor.getValue();
        assertTrue(savedTickets.isEmpty());
    }

    // если ошибка
    @Test
    void createTicketsForSession_WhenTicketRepositoryFails_ShouldHandleException() {
        hall1.setNumberSeats(10);

        when(ticketRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));

        assertDoesNotThrow(() -> {
            sessionService.createTicketsForSession(session);
        });

        verify(ticketRepository).saveAll(anyList());
    }

    // расчёт статистики для сеанса с частично купленными билетами (2 из 5 куплены)
    @Test
    void testGetSessionStats_MixedTickets() {
        UUID sessionId = UUID.randomUUID();

        Ticket t1 = new Ticket(); t1.setIsPurchased(true);
        Ticket t2 = new Ticket(); t2.setIsPurchased(true);
        Ticket t3 = new Ticket(); t3.setIsPurchased(false);
        Ticket t4 = new Ticket(); t4.setIsPurchased(false);
        Ticket t5 = new Ticket(); t5.setIsPurchased(false);

        List<Ticket> tickets = List.of(t1, t2, t3, t4, t5);

        when(ticketRepository.findBySession_SessionId(sessionId)).thenReturn(tickets);

        Map<String, Object> stats = sessionService.getSessionStats(sessionId);

        assertEquals(5, stats.get("totalSeats"));
        assertEquals(2, stats.get("purchasedTickets"));
        assertEquals(3, stats.get("availableSeats"));
        assertEquals(40.0, stats.get("occupancyRate")); // (2/5)*100 = 40%
    }

    @Test
    void testGetSessionStats_AllPurchased() {
        UUID sessionId = UUID.randomUUID();

        Ticket t1 = new Ticket(); t1.setIsPurchased(true);
        Ticket t2 = new Ticket(); t2.setIsPurchased(true);

        List<Ticket> tickets = List.of(t1, t2);

        when(ticketRepository.findBySession_SessionId(sessionId)).thenReturn(tickets);

        Map<String, Object> stats = sessionService.getSessionStats(sessionId);

        assertEquals(2, stats.get("totalSeats"));
        assertEquals(2, stats.get("purchasedTickets"));
        assertEquals(0, stats.get("availableSeats"));
        assertEquals(100.0, stats.get("occupancyRate"));
    }

    @Test
    void testGetSessionStats_NoPurchasedTickets() {
        UUID sessionId = UUID.randomUUID();

        Ticket t1 = new Ticket(); t1.setIsPurchased(false);
        Ticket t2 = new Ticket(); t2.setIsPurchased(false);
        Ticket t3 = new Ticket(); t3.setIsPurchased(false);

        List<Ticket> tickets = List.of(t1, t2, t3);

        when(ticketRepository.findBySession_SessionId(sessionId)).thenReturn(tickets);

        Map<String, Object> stats = sessionService.getSessionStats(sessionId);

        assertEquals(3, stats.get("totalSeats"));
        assertEquals(0, stats.get("purchasedTickets"));
        assertEquals(3, stats.get("availableSeats"));
        assertEquals(0.0, stats.get("occupancyRate"));
    }

    @Test
    void testGetSessionStats_EmptySession() {
        UUID sessionId = UUID.randomUUID();

        List<Ticket> tickets = Collections.emptyList();
        when(ticketRepository.findBySession_SessionId(sessionId)).thenReturn(tickets);

        Map<String, Object> stats = sessionService.getSessionStats(sessionId);

        assertEquals(0, stats.get("totalSeats"));
        assertEquals(0, stats.get("purchasedTickets"));
        assertEquals(0, stats.get("availableSeats"));
        assertEquals(0.0, stats.get("occupancyRate"));
    }
}