package com.example.cinema.cinema_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Scope("prototype")
public class CancelTicketCommand implements TicketCommand {

    private final Ticket ticket;
    private final TicketRepository ticketRepository;
    private final SessionService sessionService;

    private User previousUser;
    private DiscountType previousDiscount;
    private boolean previousPurchaseStatus;

    @Autowired
    public CancelTicketCommand(TicketRepository ticketRepository,
                               SessionService sessionService) {
        this.ticketRepository = ticketRepository;
        this.sessionService = sessionService;
        this.ticket = null;
    }

    public CancelTicketCommand initialize(Ticket ticket) {
        return new CancelTicketCommand(ticket, ticketRepository, sessionService);
    }

    private CancelTicketCommand(Ticket ticket, TicketRepository ticketRepository,
                                SessionService sessionService) {
        this.ticket = ticket;
        this.ticketRepository = ticketRepository;
        this.sessionService = sessionService;
    }

    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException(getCannotExecuteReason());
        }

        this.previousUser = ticket.getUser();
        this.previousDiscount = ticket.getDiscount();
        this.previousPurchaseStatus = ticket.getIsPurchased();

        ticket.setUser(null);
        ticket.setDiscount(DiscountType.NO_DISCOUNT);
        ticket.setIsPurchased(false);

        ticketRepository.save(ticket);
    }

    @Override
    public void undo() {
        ticket.setUser(previousUser);
        ticket.setDiscount(previousDiscount);
        ticket.setIsPurchased(previousPurchaseStatus);

        ticketRepository.save(ticket);
    }

    @Override
    public String getDescription() {
        return String.format("Отмена покупки билета: ряд %d, место %d",
                ticket.getRow(), ticket.getSeat());
    }

    @Override
    public boolean canExecute() {
        if (ticket == null) {
            return false;
        }

        if (!ticket.getIsPurchased()) {
            return false;
        }

        Session session = ticket.getSession();
        if (session == null) {
            return false;
        }

        // Можно отменять только если до начала сеанса больше часа
        LocalDateTime sessionDateTime = LocalDateTime.of(
                session.getDate().toLocalDate(),
                session.getStartTime()
        );
        LocalDateTime oneHourBefore = sessionDateTime.minusHours(1);

        return LocalDateTime.now().isBefore(oneHourBefore);
    }

    @Override
    public String getCannotExecuteReason() {
        if (ticket == null) {
            return "Билет не найден";
        }

        if (!ticket.getIsPurchased()) {
            return "Билет не был куплен";
        }

        Session session = ticket.getSession();
        if (session == null) {
            return "Сеанс не найден";
        }

        LocalDateTime sessionDateTime = LocalDateTime.of(
                session.getDate().toLocalDate(),
                session.getStartTime()
        );
        LocalDateTime oneHourBefore = sessionDateTime.minusHours(1);

        if (LocalDateTime.now().isAfter(oneHourBefore)) {
            return "Отмена возможна только за час до начала сеанса";
        }

        return "Неизвестная причина";
    }
}
