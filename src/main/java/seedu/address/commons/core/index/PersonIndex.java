package seedu.address.commons.core.index;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a zero-based or one-based index.
 *
 * Similar to {@code Index}, {@code PersonIndex} is designed as a safe wrapper to track id for persons.
 */
public class PersonIndex {
    private int zeroBasedIndex;

    /**
     * PersonIndex can only be created by calling {@link PersonIndex#fromZeroBased(int)} or
     * {@link PersonIndex#fromOneBased(int)}.
     */
    private PersonIndex(int zeroBasedIndex) {
        if (zeroBasedIndex < 0) {
            throw new IndexOutOfBoundsException();
        }

        this.zeroBasedIndex = zeroBasedIndex;
    }

    public int getZeroBased() {
        return zeroBasedIndex;
    }

    public int getOneBased() {
        return zeroBasedIndex + 1;
    }

    /**
     * Creates a new {@code PersonIndex} using a zero-based index.
     */
    public static PersonIndex fromZeroBased(int zeroBasedIndex) {
        return new PersonIndex(zeroBasedIndex);
    }

    /**
     * Creates a new {@code PersonIndex} using a one-based index.
     */
    public static PersonIndex fromOneBased(int oneBasedIndex) {
        return new PersonIndex(oneBasedIndex - 1);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonIndex otherIndex)) {
            return false;
        }

        return zeroBasedIndex == otherIndex.zeroBasedIndex;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("zeroBasedIndex", zeroBasedIndex).toString();
    }
}
