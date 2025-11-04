package com.example.cinema.cinema_app.session.state;


public class CompletedSessionState implements SessionState {
    @Override
    public String getName() {
        return "COMPLETED";
    }

    @Override
    public boolean canPurchaseTickets() {
        return false;
    }

    @Override
    public boolean canCancelTickets() {
        return false;
    }

    @Override
    public boolean canModifySession() {
        return false;
    }

    @Override
    public String getStatusMessage() {
        return "Сеанс завершен";
    }

    @Override
    public String getDescription() {
        return "Просмотр статистики доступен";
    }
}
