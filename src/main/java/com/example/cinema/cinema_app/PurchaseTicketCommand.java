package com.example.cinema.cinema_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PurchaseTicketCommand implements TicketCommand {

    private final Ticket ticket;
    private final User user;
    private final DiscountType discountType;
    private final TicketRepository ticketRepository;
    private final SessionService sessionService;

    private User previousUser;
    private DiscountType previousDiscount;
    private boolean previousPurchaseStatus;

    @Autowired
    public PurchaseTicketCommand(TicketRepository ticketRepository,
                                 SessionService sessionService) {
        this.ticketRepository = ticketRepository;
        this.sessionService = sessionService;
        this.ticket = null;
        this.user = null;
        this.discountType = null;
    }

    public PurchaseTicketCommand initialize(Ticket ticket, User user, DiscountType discountType) {
        return new PurchaseTicketCommand(ticket, user, discountType, ticketRepository, sessionService);
    }

    private PurchaseTicketCommand(Ticket ticket, User user, DiscountType discountType,
                                  TicketRepository ticketRepository, SessionService sessionService) {
        this.ticket = ticket;
        this.user = user;
        this.discountType = discountType;
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

        ticket.setUser(user);
        ticket.setDiscount(discountType);
        ticket.setIsPurchased(true);

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
        return String.format("Покупка билета: ряд %d, место %d, пользователь: %s",
                ticket.getRow(), ticket.getSeat(), user.getEmail());
    }

    @Override
    public boolean canExecute() {
        if (ticket == null || user == null) {
            return false;
        }

        if (ticket.getIsPurchased()) {
            return false;
        }

        Session session = ticket.getSession();
        if (session == null) {
            return false;
        }

        return session.getSessionState() == SessionStatus.SCHEDULED;
    }

    @Override
    public String getCannotExecuteReason() {
        if (ticket == null || user == null) {
            return "Неверные параметры команды";
        }

        if (ticket.getIsPurchased()) {
            return "Билет уже куплен";
        }

        Session session = ticket.getSession();
        if (session == null) {
            return "Сеанс не найден";
        }

        if (session.getSessionState() != SessionStatus.SCHEDULED) {
            return "Покупка билетов возможна только для запланированных сеансов";
        }

        return "Неизвестная причина";
    }
}
