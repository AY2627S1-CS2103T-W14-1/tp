package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;

/** Marks or unmarks a contact as a favourite, independently of the displayed filter. */
public class SetFavouriteCommand extends Command {
    public static final String COMMAND_WORD = "favourite";
    public static final String UNFAVOURITE_COMMAND_WORD = "unfavourite";
    public static final String MESSAGE_ADDED = "Added to favourites: %s";
    public static final String MESSAGE_REMOVED = "Removed from favourites: %s";
    public static final String MESSAGE_ALREADY_FAVOURITE = "Already in favourites: %s";
    public static final String MESSAGE_NOT_FAVOURITE = "Not in favourites: %s";

    private final String targetName;
    private final PersonIndex targetIndex;
    private final boolean isFavourite;

    /** Creates a command selecting the supplied name and suffix. */
    public SetFavouriteCommand(String targetName, PersonIndex targetIndex, boolean isFavourite) {
        this.targetName = StringUtil.toProperCase(requireNonNull(targetName));
        this.targetIndex = requireNonNull(targetIndex);
        this.isFavourite = isFavourite;
    }

    /** Returns usage matching the requested operation. */
    public static String getUsage(boolean isFavourite) {
        String word = isFavourite ? COMMAND_WORD : UNFAVOURITE_COMMAND_WORD;
        return word + ": Marks or unmarks an existing contact as a favourite.\n"
                + "Parameters: NAME[_NUMBER]\nExample: " + word + " John Tan_2";
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        ReadOnlyAddressBook addressBook = model.getAddressBook();
        Person target = addressBook.getPersonList().stream()
                .filter(person -> person.getName().fullName.equals(targetName))
                .filter(person -> person.getPersonIndex().equals(targetIndex))
                .findFirst()
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME));
        boolean wasFavourite = addressBook.getFavouriteContacts().contains(target.getDisplayName());
        model.setFavourite(target, isFavourite);
        String message;
        if (isFavourite) {
            message = wasFavourite ? MESSAGE_ALREADY_FAVOURITE : MESSAGE_ADDED;
        } else {
            message = wasFavourite ? MESSAGE_REMOVED : MESSAGE_NOT_FAVOURITE;
        }
        return new CommandResult(String.format(message, target.getDisplayName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SetFavouriteCommand otherCommand)) {
            return false;
        }
        return targetName.equals(otherCommand.targetName) && targetIndex.equals(otherCommand.targetIndex)
                && isFavourite == otherCommand.isFavourite;
    }
}
