package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class RecentContactsTest {
    private final AddressBook addressBook = new AddressBook();

    @Test
    void record_repeatVisit_movesToFrontWithoutDuplicates() {
        addressBook.addPerson(ALICE);
        addressBook.addPerson(BENSON);
        addressBook.recordPersonViewed(ALICE);
        addressBook.recordPersonViewed(BENSON);
        addressBook.recordPersonViewed(ALICE);
        assertEquals(List.of(ALICE.getDisplayName(), BENSON.getDisplayName()), addressBook.getRecentContacts());
    }

    @Test
    void record_eleventhDistinctVisit_evictsOldest() {
        List<String> visits = new ArrayList<>();
        for (int index = 0; index < 11; index++) {
            Person person = new PersonBuilder().withName("Contact " + index).build();
            addressBook.addPerson(person);
            addressBook.recordPersonViewed(person);
            visits.addFirst(person.getDisplayName());
            assertEquals(Math.min(index + 1, 10), addressBook.getRecentContacts().size());
        }
        assertEquals(visits.subList(0, 10), addressBook.getRecentContacts());
    }

    @Test
    void record_missingOrNullContact_rejectsWithoutMutation() {
        assertThrows(IllegalArgumentException.class, () -> addressBook.recordPersonViewed(ALICE));
        assertThrows(NullPointerException.class, () -> addressBook.recordPersonViewed(null));
        assertTrue(addressBook.getRecentContacts().isEmpty());
    }

    @Test
    void removeAndReadd_duplicateSuffix_doesNotInheritVisit() {
        addressBook.addPerson(ALICE);
        addressBook.addPerson(new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex());
        Person second = addressBook.getPersonList().getLast();
        addressBook.recordPersonViewed(second);
        addressBook.removePerson(second);
        addressBook.addPerson(new PersonBuilder(ALICE).withPhone("88888888").buildNoIndex());
        assertEquals(second.getDisplayName(), addressBook.getPersonList().getLast().getDisplayName());
        assertTrue(addressBook.getRecentContacts().isEmpty());
    }

    @Test
    void copyResetAndEdit_preserveHistoryAndPosition() {
        addressBook.addPerson(ALICE);
        addressBook.recordPersonViewed(ALICE);
        AddressBook copy = new AddressBook(addressBook);
        addressBook.resetData(addressBook);
        assertEquals(copy, addressBook);
        assertEquals(copy.hashCode(), addressBook.hashCode());
        addressBook.setPerson(ALICE, BENSON);
        assertEquals(List.of(BENSON.getDisplayName()), addressBook.getRecentContacts());
        assertEquals(List.of(ALICE.getDisplayName()), copy.getRecentContacts());
        addressBook.resetData(new AddressBook());
        assertTrue(addressBook.getRecentContacts().isEmpty());
    }

    @Test
    void restoreAndRead_history_isDefensiveAndNormalised() {
        addressBook.addPerson(ALICE);
        addressBook.addPerson(BENSON);
        addressBook.setRecentContacts(List.of("Deleted Person", BENSON.getDisplayName(),
                ALICE.getDisplayName(), BENSON.getDisplayName()));
        assertEquals(List.of(BENSON.getDisplayName(), ALICE.getDisplayName()), addressBook.getRecentContacts());
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getRecentContacts().clear());
    }
}
