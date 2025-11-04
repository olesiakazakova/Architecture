package com.example.cinema.cinema_app.ticket.command;

import com.example.cinema.cinema_app.ticket.DiscountType;
import com.example.cinema.cinema_app.ticket.Ticket;
import com.example.cinema.cinema_app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@Component
public class TicketCommandManager {

    private final Stack<TicketCommand> executedCommands = new Stack<>();
    private final Stack<TicketCommand> undoneCommands = new Stack<>();

    private final PurchaseTicketCommand purchaseTicketCommand;
    private final CancelTicketCommand cancelTicketCommand;

    @Autowired
    public TicketCommandManager(PurchaseTicketCommand purchaseTicketCommand,
                                CancelTicketCommand cancelTicketCommand) {
        this.purchaseTicketCommand = purchaseTicketCommand;
        this.cancelTicketCommand = cancelTicketCommand;
    }

    public CommandResult executePurchase(Ticket ticket, User user, DiscountType discountType) {
        PurchaseTicketCommand command = purchaseTicketCommand.initialize(ticket, user, discountType);
        return executeCommand(command);
    }

    public CommandResult executeCancel(Ticket ticket) {
        CancelTicketCommand command = cancelTicketCommand.initialize(ticket);
        return executeCommand(command);
    }

    private CommandResult executeCommand(TicketCommand command) {
        if (!command.canExecute()) {
            return CommandResult.failure(command.getCannotExecuteReason());
        }

        try {
            command.execute();
            executedCommands.push(command);
            undoneCommands.clear();
            return CommandResult.success(command.getDescription());
        } catch (Exception e) {
            return CommandResult.failure("Ошибка выполнения: " + e.getMessage());
        }
    }

    public CommandResult undo() {
        if (executedCommands.isEmpty()) {
            return CommandResult.failure("Нет операций для отмены");
        }

        TicketCommand command = executedCommands.pop();
        try {
            command.undo();
            undoneCommands.push(command);
            return CommandResult.success("Отменено: " + command.getDescription());
        } catch (Exception e) {
            executedCommands.push(command);
            return CommandResult.failure("Ошибка отмены: " + e.getMessage());
        }
    }


    public List<String> getCommandHistory() {
        return executedCommands.stream()
                .map(TicketCommand::getDescription)
                .collect(Collectors.toList());
    }

    public boolean canUndo() {
        return !executedCommands.isEmpty();
    }

    public void clearHistory() {
        executedCommands.clear();
        undoneCommands.clear();
    }
}
