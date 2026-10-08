package seedu.address.model;

import java.util.Map;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * The list is ordered by name and then by zero-based bucket id.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an immutable snapshot of the persons grouped by base name and zero-based id.
     */
    Map<String, Map<Integer, Person>> getPersonBuckets();

}
