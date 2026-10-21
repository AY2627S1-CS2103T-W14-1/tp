package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.SetFavouriteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Shares the existing name/suffix grammar between favourite and unfavourite. */
public class SetFavouriteCommandParser implements Parser<SetFavouriteCommand> {
    private final boolean isFavourite;

    /**
     * Creates a parser for setting a contact's favourite status.
     *
     * @param isFavourite {@code true} to favourite the contact; {@code false} to unfavourite it.
     */
    public SetFavouriteCommandParser(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    @Override
    public SetFavouriteCommand parse(String args) throws ParseException {
        try {
            NameIndexParser.NameAndIndex identifier = NameIndexParser.parse(args);
            return new SetFavouriteCommand(identifier.name(), identifier.personIndex(), isFavourite);
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetFavouriteCommand.getUsage(isFavourite)), exception);
        }
    }
}
