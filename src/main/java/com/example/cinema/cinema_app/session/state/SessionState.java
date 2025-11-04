package com.example.cinema.cinema_app.session.state;

public interface SessionState {
    String getName();
    //можно ли продавать билеты прямо сейчас
    boolean canPurchaseTickets();
    //можно ли возвращать билеты
    boolean canCancelTickets();
    //можно ли изменять сеанс
    boolean canModifySession();
    String getStatusMessage();
    String getDescription();
}
