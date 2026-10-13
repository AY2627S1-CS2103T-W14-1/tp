package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.ShowCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;

/** Parses arguments for the {@code show} command. */
public class ShowCommandParser implements Parser<ShowCommand> {

    @Override
    public ShowCommand parse(String args) throws ParseException {
        try {
            if (args == null || args.isBlank() || args.indexOf('_') != args.lastIndexOf('_')) {
                throw new ParseException("Show argument must contain a valid name and at most one index");
            }
            args = args.trim();
            String name = args;
            int oneBasedIndex = 1;
            int underscoreIndex = args.indexOf('_');
            if (underscoreIndex >= 0) {
                name = args.substring(0, underscoreIndex);
                String indexText = args.substring(underscoreIndex + 1);
                if (!name.equals(name.trim())) {
                    throw new ParseException("The person index must follow the name directly");
                }
                if (!StringUtil.isNonZeroUnsignedInteger(indexText)) {
                    throw new ParseException("Person index must be a positive integer");
                }
                try {
                    oneBasedIndex = Integer.parseInt(indexText);
                } catch (NumberFormatException exception) {
                    throw new ParseException("Person index is too large", exception);
                }
            }
            name = StringUtil.toProperCase(name);
            if (!Name.isValidName(name)) {
                throw new ParseException(Name.MESSAGE_CONSTRAINTS);
            }
            return new ShowCommand(name, PersonIndex.fromOneBased(oneBasedIndex));
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ShowCommand.MESSAGE_USAGE), exception);
        }
    }
}
