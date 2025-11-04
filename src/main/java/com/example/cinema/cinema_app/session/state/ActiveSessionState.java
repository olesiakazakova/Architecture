package com.example.cinema.cinema_app.session.state;

public class ActiveSessionState implements SessionState {
    @Override
    public String getName() {
        return "ACTIVE";
    }

    @Override
    public boolean canPurchaseTickets() {
        return true;
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
        return "Сеанс идет сейчас";
    }

    @Override
    public String getDescription() {
        return "Сеанс начался, отмена билетов невозможна";
    }
}
