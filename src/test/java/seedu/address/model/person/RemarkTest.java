package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class RemarkTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_emptyAndUnrestrictedText_preservesValue() {
        assertEquals("", new Remark("").value);
        assertEquals("你好!\nCoffee & tea", new Remark("你好!\nCoffee & tea").toString());
    }

    @Test
    public void equalsAndHashCode_compareText() {
        Remark remark = new Remark("note");
        assertEquals(remark, remark);
        assertEquals(remark, new Remark("note"));
        assertEquals(remark.hashCode(), new Remark("note").hashCode());
        assertNotEquals(remark, new Remark("other"));
        assertFalse(remark.equals(null));
        assertFalse(remark.equals("note"));
    }

    @Test
    public void person_differentRemark_changesEqualityButNotIdentity() {
        Person original = new PersonBuilder().build();
        Person edited = new PersonBuilder(original).withRemark("note").build();
        assertNotEquals(original, edited);
        assertTrue(original.isSamePerson(edited));
        assertEquals(edited, new PersonBuilder(edited).build());
    }
}
