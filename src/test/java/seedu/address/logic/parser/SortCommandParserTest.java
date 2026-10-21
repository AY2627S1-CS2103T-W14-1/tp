package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonSortField;

public class SortCommandParserTest {

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_validArguments_returnsSortCommand() throws Exception {
        assertEquals(new SortCommand(PersonSortField.NAME, false), parser.parse(" name asc "));
        assertEquals(new SortCommand(PersonSortField.PHONE, true), parser.parse("phone desc"));
        assertEquals(new SortCommand(PersonSortField.ADD_TIME, false), parser.parse("add"));
        assertEquals(new SortCommand(PersonSortField.EDIT_TIME, true), parser.parse("edit desc"));
    }

    @Test
    public void parse_missingOrInvalidArguments_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(""));
        assertThrows(ParseException.class, () -> parser.parse("address"));
        assertThrows(ParseException.class, () -> parser.parse("name sideways"));
        assertThrows(ParseException.class, () -> parser.parse("name asc extra"));
    }
}
