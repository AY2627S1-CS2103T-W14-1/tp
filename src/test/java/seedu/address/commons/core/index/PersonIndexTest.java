package seedu.address.commons.core.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests for {@link PersonIndex}. */
public class PersonIndexTest {

    @Test
    public void equalsAndHashCode_sameIndex_areEqual() {
        PersonIndex fromZeroBased = PersonIndex.fromZeroBased(2);
        PersonIndex fromOneBased = PersonIndex.fromOneBased(3);

        assertEquals(fromZeroBased, fromOneBased);
        assertEquals(fromZeroBased.hashCode(), fromOneBased.hashCode());
    }

    @Test
    public void fromOneBased_zero_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> PersonIndex.fromOneBased(0));
    }
}
