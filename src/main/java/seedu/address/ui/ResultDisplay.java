package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final Color WARNING_COLOR = Color.web("#ffb74d");

    @FXML
    private TextFlow resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.getChildren().clear();
        String[] lines = feedbackToUser.split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            Text line = new Text(lines[i]);
            line.setFill(Color.WHITE);
            if (lines[i].startsWith("Warning:")) {
                line.setFill(WARNING_COLOR);
            }
            resultDisplay.getChildren().add(line);
            if (i < lines.length - 1) {
                resultDisplay.getChildren().add(new Text("\n"));
            }
        }
    }

}
