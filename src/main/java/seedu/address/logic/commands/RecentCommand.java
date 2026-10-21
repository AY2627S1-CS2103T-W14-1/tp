package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/** Shows the ten most recently selected distinct contacts, newest first. */
public class RecentCommand extends Command {
    public static final String COMMAND_WORD = "recent";
    public static final String MESSAGE_USAGE = "recent: Lists recently viewed contacts.\nParameters: none";
    public static final String MESSAGE_EMPTY = "No recently viewed contacts yet. Use show NAME[_NUMBER] to open one.";
    public static final String MESSAGE_SUCCESS = "Listed %d recently viewed contact(s), most recent first.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.showRecentPersons();
        int count = model.getFilteredPersonList().size();
        return new CommandResult(count == 0 ? MESSAGE_EMPTY : String.format(MESSAGE_SUCCESS, count));
    }
}
