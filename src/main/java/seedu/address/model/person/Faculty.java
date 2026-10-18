package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/**
 * Represents a Professor's faculty in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidFaculty(String)}
 */
public class Faculty {

    public static final String MESSAGE_CONSTRAINTS = "Faculty can take any values, and should not be blank";

    /*
     * The first character of the faculty must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs a {@code Faculty}.
     *
     * @param faculty A valid faculty.
     */
    public Faculty(String faculty) {
        checkArgument(isValidFaculty(faculty), MESSAGE_CONSTRAINTS);
        value = faculty;
    }

    /**
     * Returns true if a given string is a valid faculty.
     */
    public static boolean isValidFaculty(String test) {
        return test == null || test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Faculty otherFaculty)) {
            return false;
        }

        return Objects.equals(value, otherFaculty.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
