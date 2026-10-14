package seedu.address.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    @FXML
    private ImageView photo;
    @FXML
    private Label photoStatus;

    public ContactDetailsWindow(Person person, Path dataFilePath) {
        this(new Stage(), person, dataFilePath);
    }

    ContactDetailsWindow(Stage root, Person person, Path dataFilePath) {
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
        setPhoto(person, dataFilePath);
    }

    /** Loads the contact photo when it is available inside the application's data directory. */
    private void setPhoto(Person person, Path dataFilePath) {
        if (person.getPhoto().isEmpty()) {
            photoStatus.setText("No photo available");
            return;
        }

        Path dataDirectory = dataFilePath.toAbsolutePath().getParent();
        Path photoPath = dataDirectory.resolve(person.getPhoto().get().getRelativePath()).normalize();
        if (!photoPath.startsWith(dataDirectory) || !Files.isRegularFile(photoPath)) {
            photoStatus.setText("Photo file is unavailable");
            return;
        }

        Image image = new Image(photoPath.toUri().toString());
        if (image.isError()) {
            photoStatus.setText("Photo file cannot be displayed");
            return;
        }

        photo.setImage(image);
        photoStatus.setText("");
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
