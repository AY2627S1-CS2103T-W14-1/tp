package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/** Tests for {@link ShowCommand}. */
public class ShowCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingPerson_success() {
        Person personToShow = model.getFilteredPersonList().getFirst();
        ShowCommand showCommand = new ShowCommand(personToShow.getName().fullName, personToShow.getPersonIndex());

        CommandResult expectedResult = new CommandResult(
                String.format(ShowCommand.SHOWING_PERSON_MESSAGE, personToShow.getDisplayName()), personToShow);
        assertCommandSuccess(showCommand, model, expectedResult, model);
    }

    @Test
    public void execute_missingPerson_throwsCommandException() {
        assertCommandFailure(new ShowCommand("Unknown Person"), model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
    }

    @Test
    public void equals() {
        ShowCommand firstCommand = new ShowCommand("Alice Pauline");
        ShowCommand secondCommand = new ShowCommand("Benson Meier");

        assertTrue(firstCommand.equals(firstCommand));
        assertTrue(firstCommand.equals(new ShowCommand("alice pauline")));
        assertFalse(firstCommand.equals(secondCommand));
        assertFalse(firstCommand.equals(null));
    }
}
