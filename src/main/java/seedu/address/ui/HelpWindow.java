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
            CampusContacts command guide

            add NAME /role ROLE /email EMAIL /phone PHONE /location LOCATION [/photo FILE_PATH] [t/TAG]...
              Adds a contact. /photo is optional; supported image types are PNG, JPG, JPEG, and GIF.
              The selected image is copied into CampusContacts and shown by the show command.
              Example: add John Tan /role student /email john@u.nus.edu /phone 90000001 /location COM1
                       /photo C:\\Users\\Student\\Pictures\\john.jpg t/teammate

            show NAME[_NUMBER]
              Opens the full details of a contact.
              Example: show John Tan or show John Tan_2

            delete NAME[_NUMBER]
              Deletes the selected contact.
              Example: delete John Tan_2

            list
              Displays every saved contact.

            clear
              Deletes every contact after confirmation. Type yes to confirm or no to cancel.

            help
              Opens this command guide.

            exit
              Closes CampusContacts.
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
