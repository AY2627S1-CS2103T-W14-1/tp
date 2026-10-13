package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ShowCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses arguments for the {@code show} command. */
public class ShowCommandParser implements Parser<ShowCommand> {

    @Override
    public ShowCommand parse(String args) throws ParseException {
        try {
            NameIndexParser.NameAndIndex nameAndIndex = NameIndexParser.parse(args);
            return new ShowCommand(nameAndIndex.name(), nameAndIndex.personIndex());
        } catch (ParseException exception) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowCommand.MESSAGE_USAGE), exception);
        }
    }
}
