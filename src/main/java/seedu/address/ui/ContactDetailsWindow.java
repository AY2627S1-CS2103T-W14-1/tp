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
    private Label role;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label locationLabel;
    @FXML
    private Label majorLabel;
    @FXML
    private Label major;
    @FXML
    private Label yearLabel;
    @FXML
    private Label year;
    @FXML
    private Label facultyLabel;
    @FXML
    private Label faculty;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label department;
    @FXML
    private Label hoursLabel;
    @FXML
    private Label hours;
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
        role.setText(person.getRole().toString());
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().value);
        locationLabel.setText(person.getAddress().value);
        major.setText(person.getMajor().value);
        year.setText(person.getYear().value);
        faculty.setText(person.getFaculty().value);
        department.setText(person.getDepartment().value);
        hours.setText(person.getHours().value);
        setFieldVisibility(person.getMajor().value != null, majorLabel, major);
        setFieldVisibility(person.getYear().value != null, yearLabel, year);
        setFieldVisibility(person.getFaculty().value != null, facultyLabel, faculty);
        setFieldVisibility(person.getDepartment().value != null, departmentLabel, department);
        setFieldVisibility(person.getHours().value != null, hoursLabel, hours);
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

    /** Hides unused or unspecified fields. */
    private void setFieldVisibility(boolean isSpecified, Label label, Label value) {
        label.setVisible(isSpecified);
        label.setManaged(isSpecified);
        value.setVisible(isSpecified);
        value.setManaged(isSpecified);
    }

    @FXML
    private void handleClose() {
        hide();
    }
}
