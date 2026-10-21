package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UnindexedPerson;
import seedu.address.model.person.UniquePersonHashMap;

/**
 * Wraps all data at the address-book level.
 * Persons with the same name and details may coexist; their bucket ids distinguish them.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonHashMap persons = new UniquePersonHashMap();
    private final Set<String> favouriteContacts = new HashSet<>();

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
        favouriteContacts.clear();
    }

    /**
     * Replaces the contents of the name buckets with data loaded from storage.
     */
    public void setPersonBuckets(Map<String, Map<Integer, Person>> personBuckets) {
        persons.setPersonBuckets(personBuckets);
        favouriteContacts.clear();
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        Set<String> favourites = newData.getFavouriteContacts();
        setPersonBuckets(newData.getPersonBuckets());
        setFavouriteContacts(favourites);
    }

    //// person-level operations

    /**
     * Returns true if a person with the same name and bucket id exists in the address book.
     */
    public boolean hasPerson(UnindexedPerson person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person receives the smallest available id in its name bucket when unindexed.
     */
    public void addPerson(UnindexedPerson p) {
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
        if (favouriteContacts.remove(target.getDisplayName())) {
            favouriteContacts.add(editedPerson.getDisplayName());
        }
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        favouriteContacts.remove(key.getDisplayName());
    }

    /** Marks or unmarks an existing contact without reconstructing its profile data. */
    public void setFavourite(Person person, boolean isFavourite) {
        requireNonNull(person);
        if (getPersonList().stream().noneMatch(person::isSamePerson)) {
            throw new IllegalArgumentException("Favourite contact must exist in the address book");
        }
        if (isFavourite) {
            favouriteContacts.add(person.getDisplayName());
        } else {
            favouriteContacts.remove(person.getDisplayName());
        }
    }

    public boolean isFavourite(Person person) {
        return favouriteContacts.contains(requireNonNull(person).getDisplayName());
    }

    /** Restores favourites, ignoring references to contacts removed by manual data-file editing. */
    public void setFavouriteContacts(Set<String> identifiers) {
        requireNonNull(identifiers);
        Set<String> retained = new HashSet<>();
        getPersonList().stream().map(Person::getDisplayName).filter(identifiers::contains).forEach(retained::add);
        favouriteContacts.clear();
        favouriteContacts.addAll(retained);
    }

    @Override
    public Set<String> getFavouriteContacts() {
        return Set.copyOf(favouriteContacts);
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

        return persons.equals(otherAddressBook.persons)
                && favouriteContacts.equals(otherAddressBook.favouriteContacts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, favouriteContacts);
    }
}
