package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonSortField;

public class SortCommandTest {

    @Test
    public void execute_sortByNameDescending_sortsDisplayedListOnly() {
        ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        CommandResult result = new SortCommand(PersonSortField.NAME, true).execute(model);

        assertEquals("Sorted persons by name in descending order.", result.getFeedbackToUser());
        assertEquals(List.of("George Best", "Fiona Kunz", "Elle Meyer", "Daniel Meier",
                "Carl Kurz", "Benson Meier", "Alice Pauline"),
                model.getFilteredPersonList().stream().map(person -> person.getName().fullName).toList());
        assertEquals(List.of("Alice Pauline", "Benson Meier", "Carl Kurz", "Daniel Meier",
                "Elle Meyer", "Fiona Kunz", "George Best"),
                model.getAddressBook().getPersonList().stream()
                        .map(person -> person.getName().fullName).toList());
    }

    @Test
    public void execute_sortByPhoneAscending_sortsDisplayedList() {
        ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        new SortCommand(PersonSortField.PHONE, false).execute(model);

        assertEquals(List.of("87652533", "94351253", "9482224", "9482427", "9482442",
                "95352563", "98765432"),
                model.getFilteredPersonList().stream().map(person -> person.getPhone().value).toList());
    }
}
