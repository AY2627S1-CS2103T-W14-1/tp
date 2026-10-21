package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/**
 * Represents a Professor's hours in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidHours(String)}
 */
public class Hours {

    public static final String MESSAGE_CONSTRAINTS = "Hours can take any values, and should not be blank";

    /*
     * The first character of the hours must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs a {@code Hours}.
     *
     * @param hours A valid hours.
     */
    public Hours(String hours) {
        checkArgument(isValidHours(hours), MESSAGE_CONSTRAINTS);
        value = hours;
    }

    /**
     * Returns true if a given string is a valid hours.
     */
    public static boolean isValidHours(String test) {
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
        if (!(other instanceof Hours otherHours)) {
            return false;
        }

        return Objects.equals(value, otherHours.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
