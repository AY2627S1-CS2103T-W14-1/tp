package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonSortField;

/** Parses arguments for the sort command. */
public class SortCommandParser implements Parser<SortCommand> {

    @Override
    public SortCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        String[] tokens = trimmedArgs.isEmpty() ? new String[0] : trimmedArgs.split("\\s+");

        if (tokens.length < 1 || tokens.length > 2) {
            throw invalidFormat();
        }

        PersonSortField field;
        try {
            field = PersonSortField.fromUserInput(tokens[0]);
        } catch (IllegalArgumentException exception) {
            throw invalidFormat();
        }

        boolean descending = false;
        if (tokens.length == 2) {
            if (tokens[1].equalsIgnoreCase("desc")) {
                descending = true;
            } else if (!tokens[1].equalsIgnoreCase("asc")) {
                throw invalidFormat();
            }
        }

        return new SortCommand(field, descending);
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }
}
