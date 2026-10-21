package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CancelCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ConfirmCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Photo;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    private Command pendingCommand;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Set<Photo> photosBeforeCommand = getReferencedPhotos();
        CommandResult commandResult;
        Command command;

        if (pendingCommand != null) {
            command = pendingCommand;
            pendingCommand = null;
            if (commandText.equals("yes")) {
                commandResult = executeCommand(command);
            } else {
                commandResult = (new CancelCommand()).execute(model);
            }
        } else {
            command = addressBookParser.parseCommand(commandText);
            if (command.requiresConfirmation()) {
                pendingCommand = command;
                commandResult = (new ConfirmCommand(command)).execute(model);
            } else {
                commandResult = executeCommand(command);
            }
        }

        List<String> previousRecentContacts = model.getAddressBook().getRecentContacts();
        if (commandResult.isShowPersonDetails()) {
            model.recordPersonViewed(commandResult.getPersonToShow());
        }
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (IOException ioe) {
            if (commandResult.isShowPersonDetails()) {
                model.restoreRecentContacts(previousRecentContacts);
            }
            if (ioe instanceof AccessDeniedException) {
                throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, ioe.getMessage()), ioe);
            }
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        deleteUnreferencedPhotos(photosBeforeCommand);

        return commandResult;
    }

    /**
     * Imports an optional add-command photo before applying the command to the model.
     */
    private CommandResult executeCommand(Command command) throws CommandException {
        if (command instanceof AddCommand addCommand && addCommand.getPhotoSource().isPresent()) {
            Photo copiedPhoto;
            try {
                copiedPhoto = storage.copyPhoto(addCommand.getPhotoSource().get());
            } catch (IOException exception) {
                throw new CommandException("Could not import the photo: " + exception.getMessage(), exception);
            }

            try {
                return addCommand.withPhoto(copiedPhoto).execute(model);
            } catch (CommandException exception) {
                deletePhotoQuietly(copiedPhoto);
                throw exception;
            }
        }
        return command.execute(model);
    }

    /** Returns every photo currently referenced by an address-book contact. */
    private Set<Photo> getReferencedPhotos() {
        return model.getAddressBook().getPersonList().stream()
                .map(Person::getPhoto)
                .flatMap(java.util.Optional::stream)
                .collect(Collectors.toSet());
    }

    /** Deletes photo files that were referenced before a successful command but are no longer referenced. */
    private void deleteUnreferencedPhotos(Set<Photo> photosBeforeCommand) {
        Set<Photo> currentPhotos = getReferencedPhotos();
        photosBeforeCommand.stream()
                .filter(photo -> !currentPhotos.contains(photo))
                .forEach(this::deletePhotoQuietly);
    }

    /** Attempts cleanup without changing the result of an otherwise completed command. */
    private void deletePhotoQuietly(Photo photo) {
        try {
            storage.deletePhoto(photo);
        } catch (IOException exception) {
            logger.warning("Could not remove unused photo " + photo + ": " + exception.getMessage());
        }
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
