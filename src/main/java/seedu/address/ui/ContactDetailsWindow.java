package seedu.address.ui;

import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seedu.address.model.person.Person;

/** Controller for a window that displays a contact's complete stored details. */
public class ContactDetailsWindow extends UiPart<Stage> {

    private static final String FXML = "ContactDetailsWindow.fxml";

    @FXML
    private Label name;
    @FXML
    private Label contactId;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label locationLabel;
    @FXML
    private Label tags;

    public ContactDetailsWindow(Person person) {
        this(new Stage(), person);
    }

    ContactDetailsWindow(Stage root, Person person) {
        super(FXML, root);
        name.setText(person.getDisplayName());
        contactId.setText(person.getPersonIndex() == null
                ? "Not assigned" : Integer.toString(person.getPersonIndex().getOneBased()));
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        locationLabel.setText(person.getAddress().value);
        tags.setText(person.getTags().isEmpty() ? "None" : person.getTags().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", ")));
    }

    /** Shows and centres the contact-details window. */
    public void show() {
        getRoot().show();
        getRoot().centerOnScreen();
    }

    public boolean isShowing() {
        return getRoot().isShowing();
    }

    public void hide() {
        getRoot().hide();
    }

    @FXML
    private void handleClose() {
        hide();
    }
}
