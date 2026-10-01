package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addReplaceAndRemoveRemark_success() {
        assertRemarkSuccess("Likes coffee");
        assertRemarkSuccess("Prefers tea");
        assertRemarkSuccess("");
    }

    @Test
    public void execute_filteredList_editsDisplayedPerson() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertRemarkSuccess("This is the second person in the full list");
    }

    @Test
    public void execute_invalidIndex_failureWithoutChanges() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new RemarkCommand(outOfBounds, new Remark("note")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new RemarkCommand(INDEX_SECOND_PERSON, new Remark("note")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editOtherFields_preservesRemark() throws Exception {
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Keep this note")).execute(model);
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setName(new Name("Updated Name"));
        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);
        assertEquals(new Remark("Keep this note"), model.getFilteredPersonList().get(0).getRemark());
    }

    @Test
    public void equals_comparesIndexAndRemark() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("note"));
        assertEquals(command, command);
        assertEquals(command, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("note")));
        assertNotEquals(command, new RemarkCommand(INDEX_SECOND_PERSON, new Remark("note")));
        assertNotEquals(command, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("other")));
        assertFalse(command.equals(null));
        assertFalse(command.equals("note"));
    }

    private void assertRemarkSuccess(String text) {
        Person original = model.getFilteredPersonList().get(0);
        Person edited = new PersonBuilder(original).withRemark(text).build();
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.setPerson(original, edited);
        String message = text.isEmpty() ? RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS
                : RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS;
        assertCommandSuccess(new RemarkCommand(INDEX_FIRST_PERSON, new Remark(text)), model,
                String.format(message, Messages.format(edited)), expected);
        assertTrue(original.isSamePerson(edited));
    }
}
