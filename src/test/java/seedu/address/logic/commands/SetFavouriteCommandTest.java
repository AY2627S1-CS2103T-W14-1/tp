package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonSortField;
import seedu.address.testutil.PersonBuilder;

class SetFavouriteCommandTest {
    private final ModelManager model = new ModelManager();

    @Test
    void execute_markHiddenContactAndRepeat_selectsFromWholeAddressBook() throws Exception {
        model.addPerson(ALICE);
        model.updateFilteredPersonList(person -> false);
        SetFavouriteCommand command = new SetFavouriteCommand("alice pauline", ALICE.getPersonIndex(), true);
        assertEquals("Favourite contact: Alice Pauline", command.execute(model).getFeedbackToUser());
        command.execute(model);
        assertEquals(Set.of(ALICE.getDisplayName()), model.getAddressBook().getFavouriteContacts());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    void execute_duplicateName_targetsOnlySpecifiedSuffix() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex());
        new SetFavouriteCommand("Alice Pauline", PersonIndex.fromOneBased(2), true).execute(model);
        new ListFavouritesCommand().execute(model);
        assertEquals(List.of("Alice Pauline_2"), model.getFilteredPersonList().stream()
                .map(Person::getDisplayName).toList());
    }

    @Test
    void execute_unfavouriteInFavouriteView_removesImmediately() throws Exception {
        model.addPerson(ALICE);
        new SetFavouriteCommand("Alice Pauline", ALICE.getPersonIndex(), true).execute(model);
        new ListFavouritesCommand().execute(model);
        SetFavouriteCommand unmark = new SetFavouriteCommand("Alice Pauline", ALICE.getPersonIndex(), false);
        assertEquals("Contact is not a favourite: Alice Pauline", unmark.execute(model).getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
        unmark.execute(model);
        assertTrue(model.getAddressBook().getFavouriteContacts().isEmpty());
    }

    @Test
    void execute_missingContactOrSuffix_doesNotChangeModel() {
        model.addPerson(ALICE);
        assertCommandFailure(new SetFavouriteCommand("Unknown Person", ALICE.getPersonIndex(), true), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
        assertCommandFailure(new SetFavouriteCommand("Alice Pauline", PersonIndex.fromOneBased(2), true), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
    }

    @Test
    void execute_favourites_replacesFilterAndRetainsSort() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.setFavourite(ALICE, true);
        model.setFavourite(BENSON, true);
        model.updateFilteredPersonList(person -> false);
        model.updatePersonListSort(PersonSortField.NAME, true);
        assertEquals("Listed 2 favourite contact(s).", new ListFavouritesCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
        model.setFavourite(BENSON, false);
        new ListCommand().execute(model);
        assertEquals(List.of(BENSON, ALICE), model.getFilteredPersonList());
    }

    @Test
    void execute_emptyFavouriteView_reportsHowToAddOne() {
        assertEquals(ListFavouritesCommand.MESSAGE_EMPTY,
                new ListFavouritesCommand().execute(model).getFeedbackToUser());
        model.addPerson(ALICE);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    void activeFavouriteView_editAndReplaceAddressBook_refreshMembership() {
        model.addPerson(ALICE);
        model.setFavourite(ALICE, true);
        model.showFavouritePersons();
        model.setPerson(ALICE, BENSON);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
        AddressBook replacement = new AddressBook();
        replacement.addPerson(ALICE);
        replacement.setFavourite(ALICE, true);
        model.setAddressBook(replacement);
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    void equals_comparesTargetAndOperation() {
        SetFavouriteCommand mark = new SetFavouriteCommand("Alice Pauline", ALICE.getPersonIndex(), true);
        assertTrue(mark.equals(mark));
        assertTrue(mark.equals(new SetFavouriteCommand("alice pauline", ALICE.getPersonIndex(), true)));
        assertFalse(mark.equals(new SetFavouriteCommand("Alice Pauline", ALICE.getPersonIndex(), false)));
        assertFalse(mark.equals(new SetFavouriteCommand("Alice Pauline", PersonIndex.fromOneBased(2), true)));
        assertFalse(mark.equals(new SetFavouriteCommand("Benson Meier", BENSON.getPersonIndex(), true)));
        assertFalse(mark.equals(null));
        assertFalse(mark.equals(new ListFavouritesCommand()));
    }
}
