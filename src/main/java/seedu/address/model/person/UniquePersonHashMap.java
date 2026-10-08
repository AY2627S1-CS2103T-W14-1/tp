package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.index.PersonIndex;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Stores persons grouped by name and indexed within each name group.
 */
public class UniquePersonHashMap implements Iterable<Person> {

    private final Map<String, PersonNameBucket> internalMap = new HashMap<>();

    private final ObservableList<Person> internalList = FXCollections.observableArrayList();
    private final ObservableList<Person> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    private static class PersonNameBucket {
        // Uses one-based index
        private final TreeMap<Integer, Person> persons = new TreeMap<>();
        private final PriorityQueue<Integer> freeIndices = new PriorityQueue<>();
        private int nextIndex = 1;

        /**
         * Returns whether this bucket contains the given indexed person.
         */
        public boolean contains(Person person) {
            return person.getPersonIndex() != null
                    && persons.containsKey(person.getPersonIndex().getOneBased());
        }

        /**
         * Adds a person to this name bucket.
         */
        public void add(Person person) {
            int index = person.getPersonIndex().getOneBased();
            persons.put(index, person);
            nextIndex = Math.max(nextIndex, index + 1);
        }

        /**
         * Removes and returns the person with the given one-based index.
         */
        public Person remove(int index) {
            Person removed = persons.remove(index);
            if (removed != null) {
                freeIndices.offer(index);
            }
            return removed;
        }

        /**
         * Returns the person with the given one-based index.
         */
        public Person get(int index) {
            return persons.get(index);
        }

        /**
         * Returns all persons in index order.
         */
        public Collection<Person> values() {
            return persons.values();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof PersonNameBucket otherBucket)) {
                return false;
            }
            return persons.equals(otherBucket.persons);
        }

        @Override
        public int hashCode() {
            return persons.hashCode();
        }

        /**
         * Allocates the smallest available one-based index.
         */
        public int allocateIndex() {
            return freeIndices.isEmpty()
                    ? nextIndex++
                    : freeIndices.poll();
        }

        /**
         * Replaces this bucket's contents with the supplied persons.
         */
        public void setPersons(Collection<Person> replacement) {
            persons.clear();
            persons.putAll(
                    replacement.stream()
                            .collect(Collectors.toMap(
                                    p -> p.getPersonIndex().getOneBased(),
                                    person -> person)));
        }
    }

    private String getNameKey(Person person) {
        return person.getName().fullName;
    }

    private PersonNameBucket getBucket(Person person) {
        return internalMap.computeIfAbsent(
                getNameKey(person),
                unused -> new PersonNameBucket());
    }

    /**
     * Returns whether a person with the same name and index exists.
     */
    public boolean contains(Person toCheck) {
        requireNonNull(toCheck);

        PersonNameBucket bucket = internalMap.get(getNameKey(toCheck));
        return bucket != null && bucket.contains(toCheck);
    }

    /**
     * Adds a person, assigning an index when the person is not yet indexed.
     */
    public void add(Person toAdd) {
        requireNonNull(toAdd);

        PersonNameBucket bucket = getBucket(toAdd);

        // Parser-created persons do not have a storage index yet. Allocate it here,
        // at the point where the person is assigned to its name bucket.
        if (toAdd.getPersonIndex() == null) {
            toAdd = toAdd.withPersonIndex(
                    PersonIndex.fromOneBased(bucket.allocateIndex()));
        }

        if (bucket.contains(toAdd)) {
            throw new DuplicatePersonException();
        }

        bucket.add(toAdd);
        refreshInternalList();
    }

    /**
     * Removes a person from its name bucket and releases its index.
     */
    public void remove(Person toRemove) {
        requireNonNull(toRemove);

        String nameKey = getNameKey(toRemove);
        PersonNameBucket bucket = internalMap.get(nameKey);

        if (bucket == null || toRemove.getPersonIndex() == null
                || bucket.remove(toRemove.getPersonIndex().getOneBased()) == null) {
            throw new PersonNotFoundException();
        }

        if (bucket.values().isEmpty()) {
            internalMap.remove(nameKey);
        }
        refreshInternalList();
    }

    /**
     * Replaces an existing person with an edited person.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        if (!contains(target)) {
            throw new PersonNotFoundException();
        }

        if (contains(editedPerson)
                && !target.isSamePerson(editedPerson)) {
            throw new DuplicatePersonException();
        }

        remove(target);
        add(editedPerson);
    }

    /**
     * Returns an unmodifiable flattened view of all persons.
     */
    public ObservableList<Person> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UniquePersonHashMap otherMap)) {
            return false;
        }
        return internalMap.equals(otherMap.internalMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalMap);
    }

    private ObservableList<Person> toObservableList() {
        ObservableList<Person> result = FXCollections.observableArrayList();

        internalMap.values().stream()
                .flatMap(bucket -> bucket.values().stream())
                .forEach(result::add);

        return result;
    }

    /** Returns an iterator over all persons in the map. */
    @Override
    public Iterator<Person> iterator() {
        return toObservableList().iterator();
    }

    /**
     * Replaces the contents with the supplied persons.
     */
    public void setPersons(List<Person> persons) {
        requireAllNonNull(persons);

        UniquePersonHashMap replacement = new UniquePersonHashMap();

        for (Person person : persons) {
            if (replacement.contains(person)) {
                throw new DuplicatePersonException();
            }
            replacement.add(person);
        }

        internalMap.clear();
        internalMap.putAll(replacement.internalMap);
        refreshInternalList();
    }

    /**
     * Refreshes the live list exposed to the model and its filtered views.
     */
    private void refreshInternalList() {
        internalList.setAll(toObservableList());
    }
}
