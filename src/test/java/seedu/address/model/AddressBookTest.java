package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonHashMap;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicateNames_acceptsDistinctIndices() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .buildNoIndex();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        addressBook.resetData(newData);
        assertEquals(2, addressBook.getPersonList().size());
        assertEquals(1, addressBook.getPersonList().get(0).getPersonIndex().getOneBased());
        assertEquals(2, addressBook.getPersonList().get(1).getPersonIndex().getOneBased());
    }

    @Test
    public void resetData_withBuckets_preservesSuffixAllocationState() {
        Person thirdAlice = new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex();
        Person replacementAlice = new PersonBuilder(ALICE).withEmail("replacement@example.com").buildNoIndex();
        AddressBook loadedAddressBook = new AddressBook();
        loadedAddressBook.setPersonBuckets(Map.of(ALICE.getName().fullName, Map.of(0, ALICE, 2, thirdAlice)));

        addressBook.resetData(loadedAddressBook);
        addressBook.addPerson(replacementAlice);

        assertEquals(Map.of(0, ALICE,
                        1, replacementAlice.withPersonIndex(seedu.address.commons.core.index.PersonIndex.fromZeroBased(1)),
                        2, thirdAlice.withPersonIndex(seedu.address.commons.core.index.PersonIndex.fromZeroBased(2))),
                addressBook.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameNameAndDifferentDetails_returnsFalse() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .buildNoIndex();
        assertFalse(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public Map<String, Map<Integer, Person>> getPersonBuckets() {
            UniquePersonHashMap buckets = new UniquePersonHashMap();
            buckets.setPersons(persons);
            return buckets.getPersonBuckets();
        }
    }

}
