package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_MAJOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_OFFICE_HOURS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_ROLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Photo;
import seedu.address.model.person.UnindexedPerson;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + "NAME "
            + PREFIX_CONTACT_ROLE + " ROLE "
            + PREFIX_CONTACT_EMAIL + " EMAIL "
            + PREFIX_CONTACT_PHONE + " PHONE "
            + PREFIX_CONTACT_LOCATION + " LOCATION "
            + "[" + PREFIX_CONTACT_MAJOR + " MAJOR] "
            + "[" + PREFIX_CONTACT_YEAR + " YEAR] "
            + "[" + PREFIX_CONTACT_FACULTY + " FACULTY] "
            + "[" + PREFIX_CONTACT_DEPARTMENT + " DEPARTMENT] "
            + "[" + PREFIX_CONTACT_OFFICE_HOURS + " HOURS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + "John Doe "
            + PREFIX_CONTACT_ROLE + " student "
            + PREFIX_CONTACT_EMAIL + " johnd@example.com "
            + PREFIX_CONTACT_PHONE + " 98765432 "
            + PREFIX_CONTACT_LOCATION + " COM1, #02-01 "
            + PREFIX_CONTACT_MAJOR + " Computer Science "
            + PREFIX_CONTACT_YEAR + " 2 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String INVALID_STUDENT_PARAMETERS = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            "Student cannot have faculty, department or office hours.");
    public static final String INVALID_PROFESSOR_PARAMETERS = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            "Professor cannot have major or year.");

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final UnindexedPerson toAdd;
    private final Optional<Path> photoSource;

    /**
     * Creates an AddCommand to add the specified {@code UnindexedPerson}.
     */
    public AddCommand(UnindexedPerson person) {
        this(person, Optional.empty());
    }

    /**
     * Creates an AddCommand with an optional source image that will be imported in a later storage step.
     */
    public AddCommand(UnindexedPerson person, Optional<Path> photoSource) {
        requireNonNull(person);
        requireNonNull(photoSource);
        toAdd = person;
        this.photoSource = photoSource;
    }

    /** Returns the optional validated source path supplied by the user. */
    public Optional<Path> getPhotoSource() {
        return photoSource;
    }

    /**
     * Returns a copy of this command whose person uses the imported photo reference.
     */
    public AddCommand withPhoto(Photo photo) {
        requireNonNull(photo);
        return new AddCommand(toAdd.withPhoto(photo));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // User-entered contacts are unindexed and receive a new bucket id. This protects
        // internal callers from attempting to insert an existing indexed record again.
        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        LocalDateTime now = LocalDateTime.now();
        Person addedPerson = model.addPerson(toAdd.withTimestamps(now, now));
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(addedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd)
                && photoSource.equals(otherAddCommand.photoSource);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .add("photoSource", photoSource)
                .toString();
    }
}
