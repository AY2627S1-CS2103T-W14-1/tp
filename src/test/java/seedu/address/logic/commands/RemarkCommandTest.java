package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class RemarkCommandTest {
    @Test
    public void execute_validIndex_storesRemark() throws Exception {
        ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        new RemarkCommand(INDEX_FIRST_PERSON, "Met at conference").execute(model);

        assertEquals("Met at conference", model.getFilteredPersonList()
                .get(INDEX_FIRST_PERSON.getZeroBased()).getRemark());
    }
}
