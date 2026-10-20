package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

class FavouriteContactsTest {
    private final AddressBook addressBook = new AddressBook();

    @Test
    void setFavourite_repeatedOperations_areIdempotent() {
        addressBook.addPerson(ALICE);
        addressBook.setFavourite(ALICE, true);
        addressBook.setFavourite(ALICE, true);
        assertEquals(Set.of(ALICE.getDisplayName()), addressBook.getFavouriteContacts());
        addressBook.setFavourite(ALICE, false);
        addressBook.setFavourite(ALICE, false);
        assertTrue(addressBook.getFavouriteContacts().isEmpty());
    }

    @Test
    void setFavourite_missingOrNullPerson_rejectsWithoutMutation() {
        assertThrows(IllegalArgumentException.class, () -> addressBook.setFavourite(ALICE, true));
        assertThrows(NullPointerException.class, () -> addressBook.setFavourite(null, true));
        assertTrue(addressBook.getFavouriteContacts().isEmpty());
    }

    @Test
    void copyAndReset_preserveFavouritesWithoutSharingMutableState() {
        addressBook.addPerson(ALICE);
        addressBook.setFavourite(ALICE, true);
        AddressBook copy = new AddressBook(addressBook);
        addressBook.resetData(addressBook);
        assertEquals(addressBook, copy);
        assertEquals(addressBook.hashCode(), copy.hashCode());
        copy.setFavourite(ALICE, false);
        assertTrue(addressBook.isFavourite(ALICE));
        assertFalse(addressBook.equals(copy));
        addressBook.resetData(new AddressBook());
        assertTrue(addressBook.getFavouriteContacts().isEmpty());
    }

    @Test
    void removeAndReadd_duplicateSuffix_doesNotInheritFavourite() {
        addressBook.addPerson(ALICE);
        addressBook.addPerson(new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex());
        Person secondAlice = addressBook.getPersonList().getLast();
        addressBook.setFavourite(secondAlice, true);
        assertFalse(addressBook.isFavourite(ALICE));
        addressBook.removePerson(secondAlice);
        addressBook.addPerson(new PersonBuilder(ALICE).withPhone("88888888").buildNoIndex());
        assertEquals(secondAlice.getDisplayName(), addressBook.getPersonList().getLast().getDisplayName());
        assertTrue(addressBook.getFavouriteContacts().isEmpty());
    }

    @Test
    void editFavourite_preservesStatusAndProfileData() {
        addressBook.addPerson(ALICE);
        addressBook.setFavourite(ALICE, true);
        addressBook.setPerson(ALICE, BENSON);
        assertEquals(Set.of(BENSON.getDisplayName()), addressBook.getFavouriteContacts());
        assertEquals(BENSON, addressBook.getPersonList().getFirst());
    }

    @Test
    void restoreAndRead_favourites_areDefensiveAndIgnoreStaleReferences() {
        addressBook.addPerson(ALICE);
        addressBook.setFavouriteContacts(Set.of(ALICE.getDisplayName(), "Deleted Person_2"));
        assertEquals(Set.of(ALICE.getDisplayName()), addressBook.getFavouriteContacts());
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getFavouriteContacts().clear());
    }
}
