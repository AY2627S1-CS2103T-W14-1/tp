package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.PERSON_INDEX_FIRST;
import static seedu.address.testutil.TypicalIndexes.PERSON_INDEX_SECOND;
import static seedu.address.testutil.TypicalIndexes.PERSON_INDEX_THIRD;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ShowCommand;

/** Tests for {@link ShowCommandParser}. */
public class ShowCommandParserTest {

    private final ShowCommandParser parser = new ShowCommandParser();

    @Test
    public void parse_validArgs_returnsShowCommand() {
        assertParseSuccess(parser, "aLiCe pAuLiNe_3", new ShowCommand("Alice Pauline", PERSON_INDEX_THIRD));
        assertParseSuccess(parser, " Alice Pauline_2", new ShowCommand("Alice Pauline", PERSON_INDEX_SECOND));
        assertParseSuccess(parser, " Alice Pauline", new ShowCommand("Alice Pauline", PERSON_INDEX_FIRST));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", message);
        assertParseFailure(parser, "alice _3", message);
        assertParseFailure(parser, "alice_0", message);
        assertParseFailure(parser, "alice_3_4", message);
    }
}
