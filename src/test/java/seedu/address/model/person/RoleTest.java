package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class RoleTest {
    @Test
    public void constructor_supportedAliases_normalizesRole() {
        String[][] aliasesByRole = {{"student", "s", "stu"}, {"professor", "p", "prof"}};
        for (String[] aliases : aliasesByRole) {
            for (String alias : aliases) {
                assertTrue(Role.isValidRole(alias));
                assertEquals(aliases[0], new Role(alias).toString());
                assertEquals(aliases[0], new Role(alias.toUpperCase(Locale.ROOT)).toString());
            }
        }
    }

    @Test
    public void constructor_invalidRole_explainsAllSupportedAliases() {
        String expectedMessage = "Role should either be student (s, stu) or professor (p, prof)";
        assertEquals(expectedMessage, Role.MESSAGE_CONSTRAINTS);
        for (String invalid : new String[]{"", " ", "staff", "students", " student"}) {
            assertFalse(Role.isValidRole(invalid));
            assertThrows(IllegalArgumentException.class, expectedMessage, () -> new Role(invalid));
        }
        assertThrows(NullPointerException.class, () -> new Role(null));
    }
}
