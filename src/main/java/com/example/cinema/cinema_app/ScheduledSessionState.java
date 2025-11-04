package com.example.cinema.cinema_app;

public class ScheduledSessionState implements SessionState {
    @Override
    public String getName() {
        return "SCHEDULED";
    }

    @Override
    public boolean canPurchaseTickets() {
        return true;
    }

    @Override
    public boolean canCancelTickets() {
        return true;
    }

    @Override
    public boolean canModifySession() {
        return true;
    }

    @Override
    public String getStatusMessage() {
        return "Сеанс запланирован";
    }

    @Override
    public String getDescription() {
        return "Билеты доступны для покупки";
    }
}
