package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes the first person matching a supplied name from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by name and optional person index.\n"
            + "Parameters: NAME[_PERSON_INDEX]\n"
            + "Example: " + COMMAND_WORD + " alex yeoh_2";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";

    private final String targetName;
    private final PersonIndex targetIndex;

    public DeleteCommand(String targetName) {
        this(targetName, PersonIndex.fromOneBased(1));
    }

    public DeleteCommand(String targetName, PersonIndex targetIndex) {
        this.targetName = StringUtil.toProperCase(requireNonNull(targetName));
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToDelete;
        personToDelete = lastShownList.stream()
                .filter(person -> person.getName().fullName.equals(targetName))
                .filter(person -> person.getPersonIndex() != null
                        && person.getPersonIndex().equals(targetIndex))
                .findFirst()
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME));
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return java.util.Objects.equals(targetName, otherDeleteCommand.targetName)
                && java.util.Objects.equals(targetIndex, otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
