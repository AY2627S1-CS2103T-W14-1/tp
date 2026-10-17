package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Person's role in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidRole(String)}
 */
public class Role {

    public static final String MESSAGE_CONSTRAINTS =
            "Role should either be Student (s) or Professor (p)";

    /*
     * The first character of the role must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX =
            "(?i)^(student|stu|s|professor|prof|p)$";

    public final String role;

    /**
     * Constructs a {@code Role}.
     *
     * @param r A valid role.
     */
    public Role(String r) {
        requireNonNull(r);
        checkArgument(isValidRole(r), MESSAGE_CONSTRAINTS);
        role = formatRole(r);
    }

    /**
     * Formats a {@code Role} in standard format.
     *
     * @param r A valid role.
     */
    public static String formatRole(String r) {
        switch (r.trim().toLowerCase(Locale.ROOT)) {
            case "student":
            case "stu":
            case "s":
                return "student";
            case "professor":
            case "prof":
            case "p":
                return "professor";
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    /**
     * Returns true if a given string is a valid role.
     */
    public static boolean isValidRole(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return role;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Role otherRole)) {
            return false;
        }

        return role.equals(otherRole.role);
    }

    @Override
    public int hashCode() {
        return role.hashCode();
    }

}
