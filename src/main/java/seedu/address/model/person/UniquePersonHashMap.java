package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Stores persons in name buckets for structured persistence while preserving the existing insertion-order list API.
 *
 * <p>A bucket maps suffix zero to the base name and positive suffixes to subsequent persons with that name. Deleted
 * suffixes are retained in a priority queue so that the smallest available suffix is reused.</p>
 */
public class UniquePersonHashMap implements Iterable<Person> {

    private final Map<String, PersonNameBucket> personsByName = new HashMap<>();
    private final ObservableList<Person> internalList = FXCollections.observableArrayList();
    private final ObservableList<Person> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /** Stores the persons and suffix allocation state for one base name. */
    private static class PersonNameBucket {
        private final TreeMap<Integer, Person> persons = new TreeMap<>();
        private final PriorityQueue<Integer> freeSuffixes = new PriorityQueue<>();
        private int nextSuffix;

        int allocateSuffix() {
            return freeSuffixes.isEmpty() ? nextSuffix++ : freeSuffixes.poll();
        }

        void releaseSuffix(int suffix) {
            freeSuffixes.offer(suffix);
        }
    }

    /** A person's location in the name-bucket structure. */
    private record PersonLocation(String baseName, int suffix) {}

    /**
     * Returns true if the exact person is already stored.
     */
    public boolean contains(Person toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Adds a person to the appropriate name bucket.
     */
    public void add(Person toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicatePersonException();
        }

        addToBucket(toAdd);
        internalList.add(toAdd);
    }

    /**
     * Replaces an existing person while retaining the displayed-list position.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        PersonLocation location = findLocation(target);
        if (location == null) {
            throw new PersonNotFoundException();
        }
        if (!target.equals(editedPerson) && contains(editedPerson)) {
            throw new DuplicatePersonException();
        }

        int displayedIndex = internalList.indexOf(target);
        if (location.baseName().equals(editedPerson.getName().fullName)) {
            personsByName.get(location.baseName()).persons.put(location.suffix(), editedPerson);
        } else {
            removeFromBucket(location);
            addToBucket(editedPerson);
        }
        internalList.set(displayedIndex, editedPerson);
    }

    /**
     * Removes the exact person and releases its suffix for reuse.
     */
    public void remove(Person toRemove) {
        requireNonNull(toRemove);

        PersonLocation location = findLocation(toRemove);
        if (location == null) {
            throw new PersonNotFoundException();
        }

        removeFromBucket(location);
        internalList.remove(toRemove);
    }

    /**
     * Replaces the contents with the persons in the supplied map.
     */
    public void setPersons(UniquePersonHashMap replacement) {
        requireNonNull(replacement);
        setPersons(replacement.internalList);
    }

    /**
     * Replaces the contents with the supplied persons in their existing display order.
     */
    public void setPersons(List<Person> persons) {
        requireAllNonNull(persons);

        UniquePersonHashMap replacement = new UniquePersonHashMap();
        for (Person person : persons) {
            replacement.add(person);
        }

        personsByName.clear();
        personsByName.putAll(replacement.personsByName);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Returns an immutable snapshot of the name-bucket map for JSON serialization.
     */
    public Map<String, Map<Integer, Person>> getPersonBuckets() {
        Map<String, Map<Integer, Person>> snapshot = new TreeMap<>();
        personsByName.forEach((name, bucket) -> snapshot.put(name,
                Collections.unmodifiableMap(new TreeMap<>(bucket.persons))));
        return Collections.unmodifiableMap(snapshot);
    }

    /**
     * Replaces the contents with the supplied name buckets loaded from storage.
     *
     * <p>The display list is rebuilt in base-name, then suffix order. Suffix allocation state is derived from the
     * occupied suffixes; it is not supplied by storage.</p>
     */
    public void setPersonBuckets(Map<String, Map<Integer, Person>> personBuckets) {
        requireNonNull(personBuckets);

        UniquePersonHashMap replacement = new UniquePersonHashMap();
        for (Map.Entry<String, Map<Integer, Person>> nameBucket : new TreeMap<>(personBuckets).entrySet()) {
            String baseName = requireNonNull(nameBucket.getKey());
            Map<Integer, Person> storedPersons = requireNonNull(nameBucket.getValue());
            PersonNameBucket bucket = replacement.personsByName.computeIfAbsent(baseName,
                    unused -> new PersonNameBucket());
            TreeMap<Integer, Person> sortedPersons = new TreeMap<>(storedPersons);

            for (Map.Entry<Integer, Person> storedPerson : sortedPersons.entrySet()) {
                int suffix = requireNonNull(storedPerson.getKey());
                Person person = requireNonNull(storedPerson.getValue());
                if (suffix < 0) {
                    throw new IllegalArgumentException("Person suffixes must be non-negative.");
                }
                if (!baseName.equals(person.getName().fullName)) {
                    throw new IllegalArgumentException("Person name must match its name-bucket key.");
                }
                if (replacement.contains(person)) {
                    throw new DuplicatePersonException();
                }
                bucket.persons.put(suffix, person);
                replacement.internalList.add(person);
            }

            rebuildAllocationState(bucket);
        }

        personsByName.clear();
        personsByName.putAll(replacement.personsByName);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Returns the insertion-ordered backing list used by existing commands and the UI.
     */
    public ObservableList<Person> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Person> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniquePersonHashMap otherUniquePersonHashMap)) {
            return false;
        }

        return internalList.equals(otherUniquePersonHashMap.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private void addToBucket(Person person) {
        String baseName = person.getName().fullName;
        PersonNameBucket bucket = personsByName.computeIfAbsent(baseName, unused -> new PersonNameBucket());
        bucket.persons.put(bucket.allocateSuffix(), person);
    }

    private PersonLocation findLocation(Person person) {
        PersonNameBucket bucket = personsByName.get(person.getName().fullName);
        if (bucket == null) {
            return null;
        }

        for (Map.Entry<Integer, Person> entry : bucket.persons.entrySet()) {
            if (entry.getValue().equals(person)) {
                return new PersonLocation(person.getName().fullName, entry.getKey());
            }
        }
        return null;
    }

    private void removeFromBucket(PersonLocation location) {
        PersonNameBucket bucket = personsByName.get(location.baseName());
        bucket.persons.remove(location.suffix());
        bucket.releaseSuffix(location.suffix());
        if (bucket.persons.isEmpty()) {
            personsByName.remove(location.baseName());
        }
    }

    private static void rebuildAllocationState(PersonNameBucket bucket) {
        bucket.freeSuffixes.clear();
        if (bucket.persons.isEmpty()) {
            bucket.nextSuffix = 0;
            return;
        }

        int largestSuffix = bucket.persons.lastKey();
        for (int suffix = 0; suffix < largestSuffix; suffix++) {
            if (!bucket.persons.containsKey(suffix)) {
                bucket.freeSuffixes.offer(suffix);
            }
        }
        bucket.nextSuffix = largestSuffix + 1;
    }
}
