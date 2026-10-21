package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person extends UnindexedPerson {

    // Duplicate handling fields
    private final PersonIndex personIndex;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Role role, Phone phone, Email email, Address address, Set<Tag> tags,
                  PersonIndex personIndex) {
        super(name, role, phone, email, address, tags);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    /**
     * Additional constructor for Student: Major and Year fields are optional (may be null).
     */
    public Person(Name name, Role role, Phone phone, Email email, Address address, Major major, Year year,
                  Set<Tag> tags, PersonIndex personIndex) {
        super(name, role, phone, email, address, major, year, tags);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    /**
     * Additional constructor for Professor: Faculty, Department and Hours fields are optional (may be null).
     */
    public Person(Name name, Role role, Phone phone, Email email, Address address, Faculty faculty,
                  Department department, Hours hours, Set<Tag> tags, PersonIndex personIndex) {
        super(name, role, phone, email, address, faculty, department, hours, tags);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    /**
     * Creates an indexed person with an optional photo reference.
     */
    public Person(Name name, Role role, Phone phone, Email email, Address address, Set<Tag> tags,
                  PersonIndex personIndex, Photo photo) {
        super(name, role, phone, email, address, tags, photo);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    /**
     * Additional constructor for Student with an optional photo reference.
     */
    public Person(Name name, Role role, Phone phone, Email email, Address address, Major major, Year year,
                  Set<Tag> tags, PersonIndex personIndex, Photo photo) {
        super(name, role, phone, email, address, major, year, tags, photo);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    /**
     * Additional constructor for Professor with an optional photo reference.
     */
    public Person(Name name, Role role, Phone phone, Email email, Address address, Faculty faculty,
                  Department department, Hours hours, Set<Tag> tags, PersonIndex personIndex, Photo photo) {
        super(name, role, phone, email, address, faculty, department, hours, tags, photo);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    public Person(Name name, Role role, Phone phone, Email email, Address address, Major major, Year year,
                  Set<Tag> tags, PersonIndex personIndex, Photo photo, LocalDateTime createdAt,
                  LocalDateTime lastEditedAt) {
        super(name, role, phone, email, address, major, year, null, null, null, tags, photo,
                createdAt, lastEditedAt);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    public Person(Name name, Role role, Phone phone, Email email, Address address, Faculty faculty,
                  Department department, Hours hours, Set<Tag> tags, PersonIndex personIndex, Photo photo,
                  LocalDateTime createdAt, LocalDateTime lastEditedAt) {
        super(name, role, phone, email, address, null, null, faculty, department, hours, tags, photo,
                createdAt, lastEditedAt);
        requireNonNull(personIndex);
        this.personIndex = personIndex;
    }

    public PersonIndex getPersonIndex() {
        return personIndex;
    }

    /**
     * Returns the display name of this person, including the duplicate suffix if indexed > 0.
     * (e.g. "John Tan" for index 0, "John Tan_2" for index 1).
     */
    @Override
    public String getDisplayName() {
        if (personIndex.hasNoSuffix()) {
            return super.getDisplayName();
        }
        String suffix = "_" + personIndex.getOneBased();
        Name name = getName();
        return name.fullName + suffix;
    }

    /**
     * Returns true if both persons have the same name & personIndex.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName())
                && Objects.equals(otherPerson.getPersonIndex(), getPersonIndex());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return super.equals(otherPerson)
                && Objects.equals(personIndex, otherPerson.personIndex);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(getName(), getRole(), getPhone(), getEmail(), getAddress(), getTags(), personIndex);
    }

    @Override
    public String toString() {
        return super.toString(); // add index later
    }

}
