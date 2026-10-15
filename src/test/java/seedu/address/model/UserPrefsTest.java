package seedu.address.model;

import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.PersonSortField;

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void sortPreference_roundTripsThroughCopy() {
        UserPrefs original = new UserPrefs();
        original.setSortField(PersonSortField.PHONE);
        original.setSortDescending(true);

        UserPrefs copy = new UserPrefs(original);

        org.junit.jupiter.api.Assertions.assertEquals(PersonSortField.PHONE, copy.getSortField());
        org.junit.jupiter.api.Assertions.assertTrue(copy.isSortDescending());
    }

}
