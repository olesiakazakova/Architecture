package com.example.cinema.cinema_app;

public interface TicketCommand {
    void execute();
    void undo();
    String getDescription();
    boolean canExecute();
    String getCannotExecuteReason();
}
