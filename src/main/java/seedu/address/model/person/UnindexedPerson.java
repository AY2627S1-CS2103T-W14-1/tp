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
    private final Role role;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final Major major;
    private final Year year;
    private final Faculty faculty;
    private final Department department;
    private final Hours hours;
    private final Photo photo;

    /**
     * Creates an unindexed person from the supplied details.
     */
    public UnindexedPerson(Name name, Role role, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, role, phone, email, address, tags, null);
    }

    /**
     * Creates an unindexed person from the supplied details and an optional photo reference.
     */
    public UnindexedPerson(Name name, Role role, Phone phone, Email email, Address address, Set<Tag> tags,
                           Photo photo) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.major = new Major(null);
        this.year = new Year(null);
        this.faculty = new Faculty(null);
        this.department = new Department(null);
        this.hours = new Hours(null);
        this.photo = photo;
    }

    /**
     * Creates an unindexed Student with additional (optional) details.
     */
    public UnindexedPerson(Name name, Role role, Phone phone, Email email, Address address, Major major, Year year,
                           Set<Tag> tags) {
        this(name, role, phone, email, address, major, year, tags, null);
    }

    /**
     * Creates an unindexed Student with additional (optional) details and an optional photo reference.
     */
    public UnindexedPerson(Name name, Role role, Phone phone, Email email, Address address, Major major, Year year,
                           Set<Tag> tags, Photo photo) {
        requireAllNonNull(name, role, phone, email, address, tags);
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.major = major != null ? major : new Major(null);
        this.year = year != null ? year : new Year(null);
        this.faculty = new Faculty(null);
        this.department = new Department(null);
        this.hours = new Hours(null);
        this.photo = photo;
    }

    /**
     * Creates an unindexed Professor with additional (optional) details.
     */
    public UnindexedPerson(Name name, Role role, Phone phone, Email email, Address address, Faculty faculty,
                           Department department, Hours hours, Set<Tag> tags) {
        this(name, role, phone, email, address, faculty, department, hours, tags, null);
    }

    /**
     * Creates an unindexed Professor with additional (optional) details and an optional photo reference.
     */
    public UnindexedPerson(Name name, Role role, Phone phone, Email email, Address address, Faculty faculty,
                           Department department, Hours hours, Set<Tag> tags, Photo photo) {
        requireAllNonNull(name, role, phone, email, address, tags);
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.major = new Major(null);
        this.year = new Year(null);
        this.faculty = faculty != null ? faculty : new Faculty(null);
        this.department = department != null ? department : new Department(null);
        this.hours = hours != null ? hours : new Hours(null);
        this.photo = photo;
    }

    /**
     * Creates an unindexed Person with some potentially null details.
     */
    public UnindexedPerson(Name name, Role role, Phone phone, Email email, Address address, Major major, Year year,
                           Faculty faculty, Department department, Hours hours, Set<Tag> tags, Photo photo) {
        requireAllNonNull(name, role, phone, email, address, tags);
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.major = major != null ? major : new Major(null);
        this.year = year != null ? year : new Year(null);
        this.faculty = faculty != null ? faculty : new Faculty(null);
        this.department = department != null ? department : new Department(null);
        this.hours = hours != null ? hours : new Hours(null);
        this.photo = photo;
    }

    /**
     * Returns a copy of this person with the supplied storage index.
     * @param newPersonIndex index assigned by the person collection
     * @return an indexed copy of this person
     */
    public Person withPersonIndex(PersonIndex newPersonIndex) {
        if (this.role.equals(new Role("student"))) {
            return new Student(name, phone, email, address, major, year, tags, newPersonIndex, photo);
        } else if (this.role.equals(new Role("professor"))) {
            return new Professor(name, phone, email, address, faculty, department, hours, tags, newPersonIndex, photo);
        } else {
            throw new IllegalArgumentException("Invalid person");
        }
    }

    /**
     * Returns a copy of this person with the supplied photo reference.
     */
    public UnindexedPerson withPhoto(Photo newPhoto) {
        return new UnindexedPerson(name, role, phone, email, address, major, year, faculty, department, hours, tags,
                newPhoto);
    }

    public Name getName() {
        return name;
    }

    public Role getRole() {
        return role;
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

    public Major getMajor() {
        return major;
    }

    public Year getYear() {
        return year;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public Department getDepartment() {
        return department;
    }

    public Hours getHours() {
        return hours;
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
                && role.equals(otherPerson.role)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && major.equals(otherPerson.major)
                && year.equals(otherPerson.year)
                && faculty.equals(otherPerson.faculty)
                && department.equals(otherPerson.department)
                && hours.equals(otherPerson.hours)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("role", role)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("major", major)
                .add("year", year)
                .add("faculty", faculty)
                .add("department", department)
                .add("hours", hours)
                .add("tags", tags)
                .toString();
    }

}
