package seedu.address.model.person;

import java.util.Set;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.model.tag.Tag;

/**
 * Represents a Professor in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Professor extends Person {

    /**
     * Every field must be present and not null.
     */
    public Professor(Name name, Phone phone, Email email, Address address, Set<Tag> tags, PersonIndex personIndex) {
        super(name, new Role("professor"), phone, email, address, tags, personIndex);
    }

}
