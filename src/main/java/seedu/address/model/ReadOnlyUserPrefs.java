package seedu.address.model;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.PersonSortField;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    PersonSortField getSortField();

    boolean isSortDescending();

}
