package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Photo;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonAddressBookStorage addressBookStorage;
    private JsonUserPrefsStorage userPrefsStorage;
    private final PhotoStorage photoStorage;

    /**
     * Creates a {@code StorageManager} with the given address book and user prefs storage.
     */
    public StorageManager(JsonAddressBookStorage addressBookStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.addressBookStorage = addressBookStorage;
        this.userPrefsStorage = userPrefsStorage;
        photoStorage = new PhotoStorage(addressBookStorage.getAddressBookFilePath());
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + addressBookStorage.getAddressBookFilePath());
        Optional<ReadOnlyAddressBook> addressBook = addressBookStorage.readAddressBook();
        if (addressBook.isPresent()) {
            Set<Photo> referencedPhotos = addressBook.get().getPersonList().stream()
                    .map(Person::getPhoto)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
            try {
                photoStorage.deleteUnreferencedPhotos(referencedPhotos);
            } catch (IOException exception) {
                logger.warning("Could not remove orphaned photo files: " + exception.getMessage());
            }
        }
        return addressBook;
    }

    @Override
    public boolean wasLegacyDataLoaded() {
        return addressBookStorage.wasLegacyDataLoaded();
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        logger.fine("Attempting to write to data file: " + addressBookStorage.getAddressBookFilePath());
        addressBookStorage.saveAddressBook(addressBook);
    }

    @Override
    public Photo copyPhoto(Path sourcePath) throws IOException {
        return photoStorage.copyPhoto(sourcePath);
    }

    @Override
    public void deletePhoto(Photo photo) throws IOException {
        photoStorage.deletePhoto(photo);
    }

    @Override
    public void deleteUnreferencedPhotos(Set<Photo> referencedPhotos) throws IOException {
        photoStorage.deleteUnreferencedPhotos(referencedPhotos);
    }

}
