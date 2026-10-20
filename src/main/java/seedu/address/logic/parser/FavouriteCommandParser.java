package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FavouriteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Shares the existing name/suffix grammar between favourite and unfavourite. */
public class FavouriteCommandParser implements Parser<FavouriteCommand> {
    private final boolean isFavourite;

    public FavouriteCommandParser(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    @Override
    public FavouriteCommand parse(String args) throws ParseException {
        try {
            NameIndexParser.NameAndIndex identifier = NameIndexParser.parse(args);
            return new FavouriteCommand(identifier.name(), identifier.personIndex(), isFavourite);
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FavouriteCommand.getUsage(isFavourite)), exception);
        }
    }
}
