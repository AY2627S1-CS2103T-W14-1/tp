package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonSortField;
import seedu.address.testutil.PersonBuilder;

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

        assertEquals(List.of("9482224", "9482427", "9482442", "87652533", "94351253", "95352563", "98765432"),
                model.getFilteredPersonList().stream().map(person -> person.getPhone().value).toList());
    }

    @Test
    public void execute_sortByAddedTimeAscending_mostRecentFirstAndUnavailableLast() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new PersonBuilder().withName("Unavailable").buildNoIndex());
        addressBook.addPerson(new PersonBuilder().withName("Old").buildNoIndex()
                .withTimestamps(LocalDateTime.of(2020, 1, 1, 0, 0), null));
        addressBook.addPerson(new PersonBuilder().withName("New").buildNoIndex()
                .withTimestamps(LocalDateTime.of(2024, 1, 1, 0, 0), null));
        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        new SortCommand(PersonSortField.ADD_TIME, false).execute(model);

        assertEquals(List.of("New", "Old", "Unavailable"), model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName).toList());
    }

    @Test
    public void execute_sortByEditedTimeDescending_oldestFirstAndUnavailableFirst() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new PersonBuilder().withName("Unavailable").buildNoIndex());
        addressBook.addPerson(new PersonBuilder().withName("Old").buildNoIndex()
                .withTimestamps(null, LocalDateTime.of(2020, 1, 1, 0, 0)));
        addressBook.addPerson(new PersonBuilder().withName("New").buildNoIndex()
                .withTimestamps(null, LocalDateTime.of(2024, 1, 1, 0, 0)));
        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        new SortCommand(PersonSortField.EDIT_TIME, true).execute(model);

        assertEquals(List.of("Unavailable", "Old", "New"), model.getFilteredPersonList().stream()
                .map(person -> person.getName().fullName).toList());
    }

    @Test
    public void execute_sortByNameDescending_reversesDuplicateNameIndexes() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(new PersonBuilder().withName("ALEX").buildNoIndex());
        addressBook.addPerson(new PersonBuilder().withName("ALEX").buildNoIndex());
        addressBook.addPerson(new PersonBuilder().withName("ALEX").buildNoIndex());
        ModelManager model = new ModelManager(addressBook, new UserPrefs());

        new SortCommand(PersonSortField.NAME, true).execute(model);

        assertEquals(List.of("ALEX_3", "ALEX_2", "ALEX"),
                model.getFilteredPersonList().stream().map(person -> person.getDisplayName()).toList());
    }

    @Test
    public void execute_sortPreferenceIsAvailableToNextModel() {
        ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        new SortCommand(PersonSortField.PHONE, true).execute(model);

        ModelManager reopenedModel = new ModelManager(model.getAddressBook(), model.getUserPrefs());

        assertEquals(model.getFilteredPersonList(), reopenedModel.getFilteredPersonList());
        assertEquals(PersonSortField.PHONE, reopenedModel.getUserPrefs().getSortField());
        assertTrue(reopenedModel.getUserPrefs().isSortDescending());
    }
}
