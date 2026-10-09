package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonHashMap;

/**
 * Wraps all data at the address-book level.
 * Persons with the same name and details may coexist; their bucket ids distinguish them.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonHashMap persons = new UniquePersonHashMap();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * Persons are assigned the next available id within their name bucket.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the name buckets with data loaded from storage.
     */
    public void setPersonBuckets(Map<String, Map<Integer, Person>> personBuckets) {
        persons.setPersonBuckets(personBuckets);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersonBuckets(newData.getPersonBuckets());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same name and bucket id exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person receives the smallest available id in its name bucket when unindexed.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The edited person must not use another person's name and bucket id.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", getPersonList())
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public Map<String, Map<Integer, Person>> getPersonBuckets() {
        return persons.getPersonBuckets();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons);
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }
}
