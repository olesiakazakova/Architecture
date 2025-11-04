package com.example.cinema.cinema_app.session.state;

public enum SessionStatus {
    SCHEDULED("Запланирован"),
    ACTIVE("Активен"),
    COMPLETED("Завершен");

    private final String description;

    SessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}