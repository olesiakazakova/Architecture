package com.example.cinema.cinema_app.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.cinema.cinema_app.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.example.cinema.cinema_app.ticket.DiscountType;
import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.film.Film;
import com.example.cinema.cinema_app.hall.Hall;
import com.example.cinema.cinema_app.session.SessionRepository;
import com.example.cinema.cinema_app.session.SessionService;
import com.example.cinema.cinema_app.ticket.TicketController;
import com.example.cinema.cinema_app.ticket.TicketRepository;
import com.example.cinema.cinema_app.ticket.command.CommandResult;
import com.example.cinema.cinema_app.ticket.command.TicketCommandManager;
import com.example.cinema.cinema_app.user.User;
import com.example.cinema.cinema_app.user.UserRepository;

// --- ТЕСТИРОВАНИЕ ДЛЯ Ticket---

class TicketTest {

    private Ticket ticket;
    private Session session;

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setSessionId(UUID.randomUUID());
        session.setCost(BigDecimal.valueOf(100.00));
        ticket = new Ticket(session, 5, 10);
    }

    // --- ТЕСТЫ ДЛЯ getFinalPrice

    @Test
    void testGetFinalPrice_NoDiscount() {
        ticket.setDiscount(DiscountType.NO_DISCOUNT);
        BigDecimal finalPrice = ticket.getFinalPrice();
        assertEquals(0, finalPrice.compareTo(BigDecimal.valueOf(100.0)));
    }

    @Test
    void testGetFinalPrice_StudentDiscount() {
        ticket.setDiscount(DiscountType.STUDENT_DISCOUNT);
        BigDecimal finalPrice = ticket.getFinalPrice();
        assertEquals(0, finalPrice.compareTo(BigDecimal.valueOf(85.0)));
    }

    @Test
    void testGetFinalPrice_ChildDiscount() {
        ticket.setDiscount(DiscountType.CHILD_DISCOUNT);
        BigDecimal finalPrice = ticket.getFinalPrice();
        assertEquals(0, finalPrice.compareTo(BigDecimal.valueOf(50.0)));
    }

    @Test
    void testGetFinalPrice_SeniorDiscount() {
        ticket.setDiscount(DiscountType.SENIOR_DISCOUNT);
        BigDecimal finalPrice = ticket.getFinalPrice();
        assertEquals(0, finalPrice.compareTo(BigDecimal.valueOf(60.0)));
    }

    // --- ТЕСТЫ ДЛЯ copy ---

    @Test
    void testCopy() {
        Ticket copy = ticket.copy();
        assertEquals(ticket.getSession(), copy.getSession());
        assertEquals(ticket.getDiscount(), copy.getDiscount());
        assertEquals(ticket.getIsPurchased(), copy.getIsPurchased());
    }
}
// --- ТЕСТИРОВАНИЕ ДЛЯ TicketController ---

// --- ТЕСТЫ ДЛЯ get AllTicket

@ExtendWith(MockitoExtension.class)
class TicketControllerGetAllTicketTest {
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionService sessionService;
    @Mock
    private TicketCommandManager commandManager;
    @Mock
    private Model model;

    @InjectMocks
    private TicketController ticketController;

    private UUID sessionId;
    private Session session;
    private List<Session> sessions;
    private List<User> users;
    private UUID ticketId;
    private String userEmail;
    private Ticket ticket;
    private User user;
    private Film film;
    private Hall hall;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();

        Film film = new Film();
        film.setFilmId(1L);
        film.setName("Test Film");

        Hall hall = new Hall();
        hall.setHallId(1);
        hall.setNumberSeats(50);

        session = new Session(film, hall,
                java.sql.Date.valueOf(LocalDate.now().plusDays(1)),
                LocalTime.of(18, 0),
                new BigDecimal("15.00"));
        session.setSessionId(sessionId);

        sessions = Arrays.asList(session);
        users = Arrays.asList(new User(), new User());

        ticketId = UUID.randomUUID();
        userEmail = "test@example.com";

        Session session = new Session();
        session.setSessionId(UUID.randomUUID());

        ticket = new Ticket(session, 1, 1);
        ticket.setTicketId(ticketId);
        ticket.setIsPurchased(false);

        user = new User();
        user.setEmail(userEmail);
        user.setName("Test User");
    }

    // получение всех билетов
    // проверка сортировки
    @Test
    void getAllTickets_WithoutSessionId_ShouldReturnAllTicketsSorted() {

        List<Ticket> allTickets = createTestTickets();
        List<Session> allSessions = Arrays.asList(session);

        when(ticketRepository.findAll()).thenReturn(allTickets);
        when(sessionRepository.findAll()).thenReturn(allSessions);
        when(userRepository.findAll()).thenReturn(users);
        when(commandManager.getCommandHistory()).thenReturn(new ArrayList<>());
        when(commandManager.canUndo()).thenReturn(true);

        Model model = new ExtendedModelMap();
        String viewName = ticketController.getAllTickets(model, null);

        assertEquals("ticket/listTickets", viewName);
        assertNull(model.getAttribute("error"));

        List<Ticket> sortedTickets = (List<Ticket>) model.getAttribute("tickets");
        assertNotNull(sortedTickets);
        assertEquals(6, sortedTickets.size());

        assertEquals(true, sortedTickets.get(0).getIsPurchased());
        assertEquals(true, sortedTickets.get(1).getIsPurchased());
        assertEquals(1, sortedTickets.get(2).getRow());
        assertEquals(1, sortedTickets.get(3).getRow());
        assertEquals(2, sortedTickets.get(4).getRow());
        assertEquals(2, sortedTickets.get(5).getRow());
    }

    // пустые данные
    @Test
    void getAllTickets_WithoutSessionId_WithEmptyTickets_ShouldHandleGracefully() {
        when(ticketRepository.findAll()).thenReturn(new ArrayList<>());
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(session));
        when(userRepository.findAll()).thenReturn(users);
        when(commandManager.getCommandHistory()).thenReturn(new ArrayList<>());
        when(commandManager.canUndo()).thenReturn(false);

        Model model = new ExtendedModelMap();
        String viewName = ticketController.getAllTickets(model, null);

        assertEquals("ticket/listTickets", viewName);
        assertNull(model.getAttribute("error"));

        List<Ticket> tickets = (List<Ticket>) model.getAttribute("tickets");
        assertNotNull(tickets);
        assertTrue(tickets.isEmpty());

        assertNotNull(model.getAttribute("cinemaSessions"));
        assertNotNull(model.getAttribute("users"));
        assertNotNull(model.getAttribute("discountTypes"));
        assertNotNull(model.getAttribute("commandHistory"));
        assertNotNull(model.getAttribute("canUndo"));
    }

    // ошибка
    @Test
    void getAllTickets_WhenRepositoryThrowsException_ShouldReturnError() {
        when(ticketRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        Model model = new ExtendedModelMap();
        String viewName = ticketController.getAllTickets(model, null);

        assertEquals("ticket/listTickets", viewName);
        String error = (String) model.getAttribute("error");
        assertNotNull(error);
        assertTrue(error.contains("Ошибка при загрузке билетов"));
    }

    private List<Ticket> createTestTickets() {
        return new ArrayList<>(Arrays.asList(
                createTicket(session, 1, 1, false),
                createTicket(session, 1, 2, true),   // купленный
                createTicket(session, 2, 1, false),
                createTicket(session, 2, 2, true),   // купленный
                createTicket(session, 1, 3, false),
                createTicket(session, 2, 3, false)
        ));
    }

    private Ticket createTicket(Session session, int row, int seat, boolean isPurchased) {
        Ticket ticket = new Ticket(session, row, seat);
        ticket.setTicketId(UUID.randomUUID());
        ticket.setIsPurchased(isPurchased);
        ticket.setDiscount(DiscountType.NO_DISCOUNT);
        return ticket;
    }
}

// --- ТЕСТЫ ДЛЯ purchaseTicket ---

@ExtendWith(MockitoExtension.class)
class TicketControllerPurchaseTicketTest{
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketCommandManager commandManager;

    @InjectMocks
    private TicketController ticketController;

    private UUID sessionId;
    private Session session;
    private List<Ticket> tickets;
    private List<Session> sessions;
    private List<User> users;
    private Film film;
    private Hall hall;
    private UUID ticketId;
    private String userEmail;
    private DiscountType discountType;
    private Ticket ticket;
    private User user;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        Film film = new Film();
        film.setFilmId(1L);
        film.setName("Test Film");

        Hall hall = new Hall();
        hall.setHallId(1);
        hall.setNumberSeats(50);

        session = new Session(film, hall,
                java.sql.Date.valueOf(LocalDate.now().plusDays(1)),
                LocalTime.of(18, 0),
                new BigDecimal("15.00"));
        session.setSessionId(sessionId);

        Ticket ticket1 = new Ticket(session,1, 1);
        ticket1.setIsPurchased(true);
        Ticket ticket2 = new Ticket(session,1, 2);
        ticket2.setIsPurchased(false);
        Ticket ticket3 = new Ticket(session,1, 1);
        ticket3.setIsPurchased(true);

        tickets = Arrays.asList(
                ticket1, ticket2, ticket3
        );

        sessions = Arrays.asList(session);
        users = Arrays.asList(new User(), new User());

        ticketId = UUID.randomUUID();
        userEmail = "test@example.com";
        discountType = DiscountType.STUDENT_DISCOUNT;

        Session session = new Session();
        session.setSessionId(UUID.randomUUID());

        ticket = new Ticket(session, 1, 1);
        ticket.setTicketId(ticketId);
        ticket.setIsPurchased(false);

        user = new User();
        user.setEmail(userEmail);
        user.setName("Test User");
    }

    // корректная покупка билетов
    @Test
    void purchaseTicket_WithValidData_ShouldPurchaseSuccessfully() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(userEmail)).thenReturn(Optional.of(user));

        CommandResult successResult = new CommandResult(true, "Билет успешно куплен");
        when(commandManager.executePurchase(ticket, user, discountType)).thenReturn(successResult);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.purchaseTicket(ticketId, userEmail, discountType, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        assertEquals("Билет успешно куплен", redirectAttributes.getFlashAttributes().get("success"));
        assertNull(redirectAttributes.getFlashAttributes().get("error"));

        verify(ticketRepository).findById(ticketId);
        verify(userRepository).findById(userEmail);
        verify(commandManager).executePurchase(ticket, user, discountType);
    }

    // ошибка, отсутствующий билет
    @Test
    void purchaseTicket_WhenTicketNotFound_ShouldReturnError() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.purchaseTicket(ticketId, userEmail, discountType, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        String error = (String) redirectAttributes.getFlashAttributes().get("error");
        assertNotNull(error);
        assertTrue(error.contains("Билет не найден"));

        verify(commandManager, never()).executePurchase(any(), any(), any());
        verify(userRepository, never()).findById(anyString());
    }

    // ошибка, пользователь не найден
    @Test
    void purchaseTicket_WhenUserNotFound_ShouldReturnError() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(userEmail)).thenReturn(Optional.empty());

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.purchaseTicket(ticketId, userEmail, discountType, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        String error = (String) redirectAttributes.getFlashAttributes().get("error");
        assertNotNull(error);
        assertTrue(error.contains("Пользователь не найден"));

        verify(commandManager, never()).executePurchase(any(), any(), any());
    }

    // обработка непредвиденных исключений
    @Test
    void purchaseTicket_WhenCommandThrowsException_ShouldReturnError() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById(userEmail)).thenReturn(Optional.of(user));

        when(commandManager.executePurchase(ticket, user, discountType))
                .thenThrow(new RuntimeException("Ошибка в команде покупки"));

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.purchaseTicket(ticketId, userEmail, discountType, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        String error = (String) redirectAttributes.getFlashAttributes().get("error");
        assertNotNull(error);
        assertTrue(error.contains("Ошибка при покупке билета"));
        assertTrue(error.contains("Ошибка в команде покупки"));
    }
}

// --- ТЕСТЫ ДЛЯ cancelTicket---

@ExtendWith(MockitoExtension.class)
class TicketControllerCancelTicket {
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketCommandManager commandManager;

    @InjectMocks
    private TicketController ticketController;

    private UUID ticketId;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();

        Session session = new Session();
        session.setSessionId(UUID.randomUUID());

        ticket = new Ticket(session, 1, 1);
        ticket.setTicketId(ticketId);
        ticket.setIsPurchased(true);
    }

    // корректная отмена
    @Test
    void cancelTicket_WithValidTicket_ShouldCancelSuccessfully() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        CommandResult successResult = new CommandResult(true, "Билет успешно отменен");
        when(commandManager.executeCancel(ticket)).thenReturn(successResult);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.cancelTicket(ticketId, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);
        assertEquals("Билет успешно отменен", redirectAttributes.getFlashAttributes().get("success"));
        assertNull(redirectAttributes.getFlashAttributes().get("error"));

        verify(ticketRepository).findById(ticketId);
        verify(commandManager).executeCancel(ticket);
    }

    // ошибка, билет не найден
    @Test
    void cancelTicket_WhenTicketNotFound_ShouldReturnError() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.cancelTicket(ticketId, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        String error = (String) redirectAttributes.getFlashAttributes().get("error");
        assertNotNull(error);
        assertTrue(error.contains("Ticket not found"));

        verify(commandManager, never()).executeCancel(any());
    }


    // отмена менее чем за 1 час до сеанса
    @Test
    void cancelTicket_WhenCommandFails_ShouldReturnErrorMessage() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        CommandResult failureResult = new CommandResult(false, "Нельзя отменить билет менее чем за 1 час до сеанса");
        when(commandManager.executeCancel(ticket)).thenReturn(failureResult);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.cancelTicket(ticketId, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);
        assertEquals("Нельзя отменить билет менее чем за 1 час до сеанса", redirectAttributes.getFlashAttributes().get("error"));
        assertNull(redirectAttributes.getFlashAttributes().get("success"));
    }

    // обработка непредвиденных исключений
    @Test
    void cancelTicket_WhenCommandThrowsException_ShouldReturnError() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        when(commandManager.executeCancel(ticket))
                .thenThrow(new RuntimeException("Ошибка при отмене билета"));

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.cancelTicket(ticketId, redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        String error = (String) redirectAttributes.getFlashAttributes().get("error");
        assertNotNull(error);
        assertTrue(error.contains("Ошибка при отмене билета"));
        assertTrue(error.contains("Ошибка при отмене билета"));
    }
}

// --- ТЕСТИРОВАНИЕ ДЛЯ undoLastCommand ---

@ExtendWith(MockitoExtension.class)
class TicketControllerUndoTest {

    @Mock
    private TicketCommandManager commandManager;

    @InjectMocks
    private TicketController ticketController;

    // корректная отмена операции
    @Test
    void undoLastCommand_WhenUndoSuccessful_ShouldReturnSuccessMessage() {
        CommandResult successResult = new CommandResult(true, "Операция успешно отменена");
        when(commandManager.undo()).thenReturn(successResult);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.undoLastCommand(redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);
        assertEquals("Операция успешно отменена", redirectAttributes.getFlashAttributes().get("success"));
        assertNull(redirectAttributes.getFlashAttributes().get("error"));

        verify(commandManager).undo();
    }

    // нет операций
    @Test
    void undoLastCommand_WhenUndoFails_ShouldReturnErrorMessage() {
        CommandResult failureResult = new CommandResult(false, "Нет операций для отмены");
        when(commandManager.undo()).thenReturn(failureResult);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.undoLastCommand(redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);
        assertEquals("Нет операций для отмены", redirectAttributes.getFlashAttributes().get("error"));
        assertNull(redirectAttributes.getFlashAttributes().get("success"));

        verify(commandManager).undo();
    }

    // обработка непредвиденных исключений
    @Test
    void undoLastCommand_WhenCommandManagerThrowsException_ShouldReturnError() {
        when(commandManager.undo()).thenThrow(new RuntimeException("Ошибка в command manager"));

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.undoLastCommand(redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        String error = (String) redirectAttributes.getFlashAttributes().get("error");
        assertNotNull(error);
        assertTrue(error.contains("Ошибка при отмене операции"));
        assertTrue(error.contains("Ошибка в command manager"));

        verify(commandManager).undo();
    }
}


// --- ТЕСТЫ ДЛФ clearHistory ---

@ExtendWith(MockitoExtension.class)
class TicketControllerClearHistory{
    @Mock
    private TicketCommandManager commandManager;

    @InjectMocks
    private TicketController ticketController;

    // успешная очистка истории
    @Test
    void clearCommandHistory_WhenSuccessful_ShouldReturnSuccessMessage() {
        doNothing().when(commandManager).clearHistory();

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.clearCommandHistory(redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);
        assertEquals("История операций очищена", redirectAttributes.getFlashAttributes().get("success"));
        assertNull(redirectAttributes.getFlashAttributes().get("error"));

        verify(commandManager).clearHistory();
    }

    // обработка непредвиденнхы ошибок
    @Test
    void clearCommandHistory_WhenCommandManagerThrowsException_ShouldReturnError() {
        doThrow(new RuntimeException("Ошибка очистки базы данных"))
                .when(commandManager).clearHistory();

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String redirectUrl = ticketController.clearCommandHistory(redirectAttributes);

        assertEquals("redirect:/tickets", redirectUrl);

        String error = (String) redirectAttributes.getFlashAttributes().get("error");
        assertNotNull(error);
        assertTrue(error.contains("Ошибка при очистке истории"));
        assertTrue(error.contains("Ошибка очистки базы данных"));

        verify(commandManager).clearHistory();
    }
}

// --- ТЕСТЫ ДЛЯ calculatePrice ---

@ExtendWith(MockitoExtension.class)
class TicketControllerCalculatePriceTest {

    @Mock
    private TicketCommandManager commandManager;

    @InjectMocks
    private TicketController ticketController;

    // расчет цены для разных типов скидок
    @Test
    void calculatePrice_WithStudentDiscount_ShouldReturnCorrectResult() {
        BigDecimal basePrice = new BigDecimal("100.00");
        DiscountType discountType = DiscountType.STUDENT_DISCOUNT;

        Map<String, Object> result = ticketController.calculatePrice(basePrice, discountType);

        assertTrue((Boolean) result.get("success"));
        assertEquals(basePrice, result.get("basePrice"));
        assertEquals(0, basePrice.compareTo((BigDecimal) result.get("basePrice")));
        assertEquals(0, new BigDecimal("85.00").compareTo((BigDecimal) result.get("finalPrice")));
        assertEquals(0, new BigDecimal("15.00").compareTo((BigDecimal) result.get("discountAmount")));
        assertEquals(0, new BigDecimal("15.00").compareTo((BigDecimal) result.get("discountPercent")));
        assertEquals("STUDENT_DISCOUNT", result.get("ticketTypeName"));
    }

    @Test
    void calculatePrice_WithChildDiscount_ShouldReturnCorrectResult() {
        BigDecimal basePrice = new BigDecimal("100.00");
        DiscountType discountType = DiscountType.CHILD_DISCOUNT;

        Map<String, Object> result = ticketController.calculatePrice(basePrice, discountType);

        assertTrue((Boolean) result.get("success"));
        assertEquals(0, basePrice.compareTo((BigDecimal) result.get("basePrice")));
        assertEquals(0, new BigDecimal("50.00").compareTo((BigDecimal) result.get("finalPrice")));
        assertEquals(0, new BigDecimal("50.00").compareTo((BigDecimal) result.get("discountAmount")));
        assertEquals(0, new BigDecimal("50.00").compareTo((BigDecimal) result.get("discountPercent")));
        assertEquals("CHILD_DISCOUNT", result.get("ticketTypeName"));
    }

    @Test
    void calculatePrice_WithSeniorDiscount_ShouldReturnCorrectResult() {
        BigDecimal basePrice = new BigDecimal("100.00");
        DiscountType discountType = DiscountType.SENIOR_DISCOUNT;

        Map<String, Object> result = ticketController.calculatePrice(basePrice, discountType);

        assertTrue((Boolean) result.get("success"));
        assertEquals(0, basePrice.compareTo((BigDecimal) result.get("basePrice")));
        assertEquals(0, new BigDecimal("60.00").compareTo((BigDecimal) result.get("finalPrice")));
        assertEquals(0, new BigDecimal("40.00").compareTo((BigDecimal) result.get("discountAmount")));
        assertEquals(0, new BigDecimal("40.00").compareTo((BigDecimal) result.get("discountPercent")));
        assertEquals("SENIOR_DISCOUNT", result.get("ticketTypeName"));
    }

    @Test
    void calculatePrice_WithNoDiscount_ShouldReturnFullPrice() {
        BigDecimal basePrice = new BigDecimal("100.00");
        DiscountType discountType = DiscountType.NO_DISCOUNT;

        Map<String, Object> result = ticketController.calculatePrice(basePrice, discountType);

        assertTrue((Boolean) result.get("success"));
        assertEquals(0, basePrice.compareTo((BigDecimal) result.get("basePrice")));
        assertEquals(0, basePrice.compareTo((BigDecimal) result.get("finalPrice")));
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) result.get("discountAmount")));
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) result.get("discountPercent")));
        assertEquals("NO_DISCOUNT", result.get("ticketTypeName"));
    }

    // корректное округление цен
    @Test
    void calculatePrice_WithRounding_ShouldRoundCorrectly() {
        BigDecimal basePrice = new BigDecimal("99.99");
        DiscountType discountType = DiscountType.STUDENT_DISCOUNT;

        Map<String, Object> result = ticketController.calculatePrice(basePrice, discountType);

        assertTrue((Boolean) result.get("success"));
        BigDecimal discountPercent = (BigDecimal) result.get("discountPercent");
        assertEquals(2, discountPercent.scale());
        assertEquals(new BigDecimal("15.00"), discountPercent);
    }

    // некорректные цены
    @Test
    void calculatePrice_WithZeroBasePrice_ShouldReturnError() {
        BigDecimal basePrice = BigDecimal.ZERO;
        DiscountType discountType = DiscountType.STUDENT_DISCOUNT;

        Map<String, Object> result = ticketController.calculatePrice(basePrice, discountType);

        assertFalse((Boolean) result.get("success"));
        assertNotNull(result.get("error"));
    }

    @Test
    void calculatePrice_WithNegativeBasePrice_ShouldReturnError() {
        BigDecimal basePrice = new BigDecimal("-50.00");
        DiscountType discountType = DiscountType.STUDENT_DISCOUNT;

        Map<String, Object> result = ticketController.calculatePrice(basePrice, discountType);

        assertFalse((Boolean) result.get("success"));
        assertNotNull(result.get("error"));
    }
}