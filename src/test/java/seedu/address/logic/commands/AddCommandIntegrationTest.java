package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.UnindexedPerson;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person addedPerson = expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(addedPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePersonAndIndex_throwsCommandException() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePerson_success() {
        Person personInList = model.getAddressBook().getPersonList().get(0);
        UnindexedPerson sameInfoPerson = new PersonBuilder(personInList).buildNoIndex();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person addedPerson = expectedModel.addPerson(sameInfoPerson);
        assertCommandSuccess(new AddCommand(sameInfoPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(addedPerson)),
                expectedModel);
    }

    @Test
    public void execute_sameNamePerson_reportsAssignedSuffix() throws Exception {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        UnindexedPerson firstJohn = new PersonBuilder().withName("John Tan").withPhone("90000001").buildNoIndex();
        UnindexedPerson secondJohn = new PersonBuilder().withName("John Tan").withPhone("90000002").buildNoIndex();

        new AddCommand(firstJohn).execute(emptyModel);
        CommandResult result = new AddCommand(secondJohn).execute(emptyModel);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS,
                Messages.format(secondJohn.withPersonIndex(PersonIndex.fromOneBased(2)))), result.getFeedbackToUser());
        assertEquals("John Tan_2", emptyModel.getAddressBook().getPersonList().get(1).getDisplayName());
    }

}
