package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Adds a remark to a person identified by the displayed index. */
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_REMARK_PERSON_SUCCESS = "Added remark to: %1$s";

    private final Index index;
    private final String remark;

    /**
     * Creates a command that adds a remark to the person at the specified index.
     *
     * @param index index of the person in the filtered person list
     * @param remark remark to add to the person
     */
    public RemarkCommand(Index index, String remark) {
        requireAllNonNull(index, remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person target = persons.get(index.getZeroBased());
        Person updated = new Person(target.getName(), target.getPhone(), target.getEmail(),
                target.getAddress(), target.getTags(), remark);
        model.setPerson(target, updated);
        return new CommandResult(String.format(MESSAGE_REMARK_PERSON_SUCCESS, Messages.format(updated)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RemarkCommand otherRemarkCommand)) {
            return false;
        }
        return index.equals(otherRemarkCommand.index) && remark.equals(otherRemarkCommand.remark);
    }
}
