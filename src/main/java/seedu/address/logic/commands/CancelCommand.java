package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Command that reports the cancellation of a pending operation.
 */
public class CancelCommand extends Command {
    public static final String CANCEL_MESSAGE = "Operation cancelled.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(CANCEL_MESSAGE);
    }
}
