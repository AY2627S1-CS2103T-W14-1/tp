package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.person.PersonSortField;

class RecentCommandTest {
    private final ModelManager model = new ModelManager();

    @Test
    void execute_emptyHistory_explainsHowToCreateHistory() {
        assertEquals(RecentCommand.MESSAGE_EMPTY, new RecentCommand().execute(model).getFeedbackToUser());
    }

    @Test
    void execute_recent_replacesFilterAndSortWithoutOverwritingPreference() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.recordPersonViewed(BENSON);
        model.recordPersonViewed(ALICE);
        model.updatePersonListSort(PersonSortField.NAME, true);
        model.updateFilteredPersonList(person -> false);
        assertEquals("Listed 2 recently viewed contact(s), most recent first.",
                new RecentCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());
        model.recordPersonViewed(BENSON);
        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
        new ListCommand().execute(model);
        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
        assertEquals(true, model.getUserPrefs().isSortDescending());
    }

    @Test
    void execute_sort_reordersCurrentMatchesAndRecentRestoresMru() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.recordPersonViewed(ALICE);
        model.recordPersonViewed(BENSON);
        new RecentCommand().execute(model);
        new SortCommand(PersonSortField.NAME, false).execute(model);
        assertEquals(List.of(ALICE, BENSON), model.getFilteredPersonList());
        new RecentCommand().execute(model);
        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
    }

    @Test
    void parse_validAndUnexpectedArguments_enforcesNoArgumentContract() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        assertInstanceOf(RecentCommand.class, parser.parseCommand("recent   "));
        assertThrows(ParseException.class, () -> parser.parseCommand("recent 10"));
        assertThrows(ParseException.class, () -> parser.parseCommand("recent John"));
    }

    @Test
    void activeRecentView_editAndReplaceAddressBook_refreshMembership() {
        model.addPerson(ALICE);
        model.recordPersonViewed(ALICE);
        model.showRecentPersons();
        model.setPerson(ALICE, BENSON);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        AddressBook replacement = new AddressBook();
        replacement.addPerson(ALICE);
        replacement.recordPersonViewed(ALICE);
        model.setAddressBook(replacement);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }
}
