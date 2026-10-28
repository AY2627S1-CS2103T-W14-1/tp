package seedu.address.model;

import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonSortField;
import seedu.address.model.person.UnindexedPerson;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if an exactly matching person exists in the address book.
     */
    boolean hasPerson(UnindexedPerson person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(UnindexedPerson person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson} and returns the stored replacement.
     * A same-name edit retains its id; a renamed person receives the smallest available id in the new bucket.
     * {@code target} must exist in the address book.
     */
    Person setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /** Updates and persists the sort used for the displayed person list. */
    void updatePersonListSort(PersonSortField field, boolean descending);

    /** Records a successful profile selection in the bounded history. */
    void recordPersonViewed(Person person);

    /** Restores history when saving a profile selection fails. */
    void restoreRecentContacts(List<String> identifiers);

    /** Replaces the current view with recent contacts in most-recent-first order. */
    void showRecentPersons();

    /** Updates the favourite status of an existing contact. */
    void setFavourite(Person person, boolean isFavourite);

    /** Shows only favourite contacts, in the selected sort order. */
    void showFavouritePersons();
}
