package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.person.PersonSortField;

/** Sorts the displayed persons by a supported field. */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts displayed persons by name or phone.\n"
            + "Parameters: FIELD [asc|desc]\n"
            + "Example: " + COMMAND_WORD + " name desc";
    public static final String MESSAGE_SUCCESS_FORMAT = "Sorted persons by %s in %sending order.";

    private final PersonSortField field;
    private final boolean descending;

    /**
     * Creates a command that sorts persons by the given field and direction.
     *
     * @param field field to sort by
     * @param descending whether to sort in descending order
     */
    public SortCommand(PersonSortField field, boolean descending) {
        this.field = requireNonNull(field);
        this.descending = descending;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updatePersonListSort(field, descending);

        String direction = descending ? "desc" : "asc";
        return new CommandResult(String.format(MESSAGE_SUCCESS_FORMAT, field, direction));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SortCommand otherSortCommand)) {
            return false;
        }
        return field == otherSortCommand.field && descending == otherSortCommand.descending;
    }
}
