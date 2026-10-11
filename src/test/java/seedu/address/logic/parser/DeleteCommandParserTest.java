package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.commons.core.index.PersonIndex;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. The parser normalizes names to proper case
 * before constructing the command.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "aLiCe pAuLiNe_3", new DeleteCommand("Alice Pauline",
                PersonIndex.fromOneBased(3)));
        assertParseSuccess(parser, " Alice Pauline_2", new DeleteCommand("Alice Pauline",
                PersonIndex.fromOneBased(2)));
        assertParseSuccess(parser, " Alice Pauline_1", new DeleteCommand("Alice Pauline",
                PersonIndex.fromOneBased(1)));
        assertParseSuccess(parser, " Alice Pauline", new DeleteCommand("Alice Pauline",
                PersonIndex.fromOneBased(1)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", message);
        assertParseFailure(parser, "alice _3", message);
        assertParseFailure(parser, "_3", message);
        assertParseFailure(parser, "alice#p_3", message);
        assertParseFailure(parser, "alice_0", message);
        assertParseFailure(parser, "alice_3_4", message);
    }
}
