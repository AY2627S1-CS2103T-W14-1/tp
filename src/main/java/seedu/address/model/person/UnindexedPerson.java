package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Carries data for a Person to be added to the address book.
 * Guarantees: details except index are present and not null, field values are validated, immutable.
 */
public class UnindexedPerson {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final Photo photo;

    /**
     * Creates an unindexed person from the supplied details.
     */
    public UnindexedPerson(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, null);
    }

    /**
     * Creates an unindexed person from the supplied details and an optional photo reference.
     */
    public UnindexedPerson(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Photo photo) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.photo = photo;
    }

    /**
     * Returns a copy of this person with the supplied storage index.
     * @param newPersonIndex index assigned by the person collection
     * @return an indexed copy of this person
     */
    public Person withPersonIndex(PersonIndex newPersonIndex) {
        return new Person(name, phone, email, address, tags, newPersonIndex, photo);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns the optional relative path to this person's copied photo.
     */
    public Optional<Photo> getPhoto() {
        return Optional.ofNullable(photo);
    }

    /**
     * Returns the display name of this person, including the duplicate suffix if indexed > 0.
     * (e.g. "John Tan" for index 0, "John Tan_2" for index 1).
     */
    public String getDisplayName() {
        return name.fullName;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
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
        if (!(other instanceof UnindexedPerson otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
