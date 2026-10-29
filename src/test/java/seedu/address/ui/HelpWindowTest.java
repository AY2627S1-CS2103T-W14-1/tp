package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class HelpWindowTest {
    @Test
    public void helpMessage_add_explainsRoleSpecificFields() {
        String addHelp = HelpWindow.HELP_MESSAGE.substring(
                HelpWindow.HELP_MESSAGE.indexOf("1. ADD A CONTACT"),
                HelpWindow.HELP_MESSAGE.indexOf("2. SHOW CONTACT DETAILS"));
        assertTrue(addHelp.contains("Students may use /major and /year only."));
        assertTrue(addHelp.contains("Professors may use /faculty, /department and /hours only."));
        assertTrue(addHelp.contains(
                "These role-specific fields are optional; fields for the other role are rejected."));
    }
}
