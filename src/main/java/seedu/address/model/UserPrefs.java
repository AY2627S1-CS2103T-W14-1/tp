package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.PersonSortField;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private PersonSortField sortField = PersonSortField.NAME;
    private boolean sortDescending;

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        setGuiSettings(userPrefs.getGuiSettings());
        setSortField(userPrefs.getSortField());
        setSortDescending(userPrefs.isSortDescending());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    public PersonSortField getSortField() {
        return sortField;
    }

    public void setSortField(PersonSortField sortField) {
        this.sortField = requireNonNull(sortField);
    }

    public boolean isSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs otherUserPrefs)) {
            return false;
        }

        return guiSettings.equals(otherUserPrefs.guiSettings)
                && sortField == otherUserPrefs.sortField
                && sortDescending == otherUserPrefs.sortDescending;
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, sortField, sortDescending);
    }

    @Override
    public String toString() {
        return "Gui Settings : " + guiSettings
                + ", Sort Field : " + sortField
                + ", Sort Descending : " + sortDescending;
    }

}
