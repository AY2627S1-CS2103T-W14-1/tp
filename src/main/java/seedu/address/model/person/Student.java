package seedu.address.model.person;

import java.time.LocalDateTime;
import java.util.Set;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.model.tag.Tag;

/**
 * Represents a Student in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Student extends Person {

    /**
     * Every field must be present and not null.
     */
    public Student(Name name, Phone phone, Email email, Address address, Set<Tag> tags, PersonIndex personIndex,
                   Photo photo) {
        super(name, new Role("student"), phone, email, address, tags, personIndex, photo);
    }

    /**
     * Additional constructor: Major and Year fields are optional (may be null).
     */
    public Student(Name name, Phone phone, Email email, Address address, Major major, Year year, Set<Tag> tags,
                   PersonIndex personIndex, Photo photo) {
        super(name, new Role("student"), phone, email, address, major, year, tags, personIndex, photo);
    }

    public Student(Name name, Phone phone, Email email, Address address, Major major, Year year, Set<Tag> tags,
                   PersonIndex personIndex, Photo photo, LocalDateTime createdAt, LocalDateTime lastEditedAt) {
        super(name, new Role("student"), phone, email, address, major, year, tags, personIndex, photo,
                createdAt, lastEditedAt);
    }
}
