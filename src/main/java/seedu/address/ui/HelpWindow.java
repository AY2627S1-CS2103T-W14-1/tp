package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String HELP_MESSAGE = """
            CAMPUSCONTACTS — COMMAND GUIDE

            Type command names without a leading slash.
            Square-bracketed options are optional.
            Exception: type [blank] literally to remove a photo.

            1. ADD A CONTACT
            Syntax:
              add NAME /role ROLE /email EMAIL /phone PHONE /location LOCATION
              [/major MAJOR] [/year YEAR] [/faculty FACULTY]
              [/department DEPARTMENT] [/hours HOURS] [/photo FILE_PATH]
              [t/TAG]...
            Students may use /major and /year only.
            Professors may use /faculty, /department and /hours only.
            These role-specific fields are optional; fields for the other role are rejected.
            Same-name contacts get the smallest available suffix:
            John Tan, John Tan_2, and so on. Deleted suffixes can be reused.
            Photos are optional. Supported types: PNG, JPG, JPEG, GIF.
            Example:
              add John Tan /role student /email john@u.nus.edu
                  /phone 90000001 /location COM1
                  /photo C:\\Users\\Student\\Pictures\\john.jpg

            2. SHOW CONTACT DETAILS
            Syntax: show NAME[_NUMBER]
            Opens the contact's full details.
            Example: show John Tan_2

            3. EDIT A CONTACT
            Syntax:
              edit NAME[_INDEX] [/name NAME] [/phone PHONE] [/email EMAIL]
              [/location LOCATION] [/major MAJOR] [/year YEAR]
              [/faculty FACULTY] [/department DEPARTMENT] [/hours HOURS]
              [/photo FILE_PATH] [t/TAG]...
            Use /photo FILE_PATH to add or replace a photo.
            Use /photo [blank] to remove it.
            Renaming assigns the smallest available suffix for the new name.
            Examples:
              edit Alex Yeoh /phone 91234567
              edit Alex Yeoh /photo C:\\Users\\Student\\Pictures\\new.jpg
              edit Alex Yeoh /photo [blank]

            4. DELETE A CONTACT
            Syntax: delete NAME[_NUMBER]
            Works even when a filter hides the contact.
            Without _NUMBER, targets only the unsuffixed identifier (NAME).
            Example: delete John Tan_2

            5. FIND CONTACTS
            Syntax: find NAME
                    or find /name NAME
            Optional filters: [/role ROLE] [/phone PHONE] [/email EMAIL]
                              [/location LOCATION] [/major MAJOR] [/year YEAR]
                              [/faculty FACULTY] [/department DEPARTMENT]
                              [/hours HOURS] [t/TAG]...
            Searches contact details for matching criteria.
            Example: find /name John /location COM1

            6. LIST ALL CONTACTS
            Syntax: list
            Shows every saved contact.

            7. SORT CONTACTS
            Syntax: sort FIELD [asc|desc]
            Fields: name, phone, add, edit. Default order: ascending.
            Example: sort name desc

            8. LIST RECENT CONTACTS
            Syntax: recent
            Shows up to ten distinct contacts opened with show,
            most recent first. Recent history is saved across restarts.

            9. FAVOURITE OR UNFAVOURITE A CONTACT
            Syntax: favourite NAME[_NUMBER]
                    unfavourite NAME[_NUMBER]
            Works even when a filter hides the contact.
            Example: favourite John Tan_2

            10. LIST FAVOURITE CONTACTS
            Syntax: favourites
            Shows favourites in the selected sort order.

            11. CLEAR ALL CONTACTS
            Syntax: clear
            Type yes to confirm or no to cancel.

            12. OPEN THIS GUIDE
            Syntax: help

            13. EXIT THE APP
            Syntax: exit
            """;

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private TextArea helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

}
