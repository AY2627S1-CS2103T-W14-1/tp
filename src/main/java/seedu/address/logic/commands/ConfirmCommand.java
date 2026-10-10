package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Command that displays a confirmation prompt for a pending command.
 */
public class ConfirmCommand extends Command {
    public static final String DEFAULT_MESSAGE = "Unknown message.";

    private final Command pendingCommand;

    public ConfirmCommand(Command command) {
        this.pendingCommand = command;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        String message = pendingCommand.getConfirmationMessage();
        if (message == null) {
            message = DEFAULT_MESSAGE;
        }
        return new CommandResult(message);
    }
}
