package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a person's remark. Empty remarks are allowed.
 */
public class Remark {
    public final String value;

    /**
     * Creates a remark with the given non-null text.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Remark otherRemark && value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
