package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/** Replaces the current filter with the user's favourite contacts. */
public class FavouritesCommand extends Command {
    public static final String COMMAND_WORD = "favourites";
    public static final String MESSAGE_USAGE = "favourites: Lists favourite contacts.\nParameters: none";
    public static final String MESSAGE_EMPTY = "No favourite contacts yet. Use favourite NAME[_NUMBER] to add one.";
    public static final String MESSAGE_SUCCESS = "Listed %d favourite contact(s).";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.showFavouritePersons();
        int count = model.getFilteredPersonList().size();
        return new CommandResult(count == 0 ? MESSAGE_EMPTY : String.format(MESSAGE_SUCCESS, count));
    }
}
