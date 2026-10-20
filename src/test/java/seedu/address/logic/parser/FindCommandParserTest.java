package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesCriteriaPredicate(List.of("Alice", "Bob"),
                        List.of(), List.of(), List.of(), List.of()));

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_validPrefixArgs_returnsFindCommand() {
        // search with /name, /phone, /location
        FindCommand expectedFindCommand =
                new FindCommand(new PersonMatchesCriteriaPredicate(List.of("Alice"),
                        List.of("9123"), List.of(), List.of("COM1"), List.of("friend")));
        assertParseSuccess(parser, " /name Alice /phone 9123 /location COM1 t/friend", expectedFindCommand);
    }

    @Test
    public void parse_bothPreambleAndNamePrefix_throwsParseException() {
        assertParseFailure(parser, "Alice /name Bob",
                "Please specify name either directly or using /name, not both.\n"
                        + FindCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyPrefixValue_throwsParseException() {
        assertParseFailure(parser, " /location ",
                "Location search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
    }

}
