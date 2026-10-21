package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

class FavouriteWorkflowTest {
    @TempDir
    private Path tempDir;

    @Test
    void execute_markRestartDeleteAndClear_keepsSelectionsConsistent() throws Exception {
        ModelManager model = new ModelManager();
        JsonAddressBookStorage addressStorage = new JsonAddressBookStorage(tempDir.resolve("contacts.json"));
        StorageManager storage = new StorageManager(addressStorage,
                new JsonUserPrefsStorage(tempDir.resolve("preferences.json")));
        Logic logic = new LogicManager(model, storage);
        logic.execute("add John Tan /role student /email first@u.nus.edu /phone 90000001 /location COM1");
        logic.execute("add John Tan /role student /email second@u.nus.edu /phone 90000002 /location COM2");
        logic.execute("favourite John Tan_2");

        ModelManager restarted = new ModelManager(addressStorage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic restartedLogic = new LogicManager(restarted, storage);
        restartedLogic.execute("favourites");
        assertEquals("John Tan_2", restarted.getFilteredPersonList().getFirst().getDisplayName());
        restartedLogic.execute("delete John Tan_2");
        restartedLogic.execute("add John Tan /role student /email third@u.nus.edu /phone 90000003 /location COM3");
        assertTrue(restarted.getAddressBook().getFavouriteContacts().isEmpty());
        restartedLogic.execute("favourite John Tan");
        restartedLogic.execute("clear");
        restartedLogic.execute("no");
        assertEquals(Set.of("John Tan"), restarted.getAddressBook().getFavouriteContacts());
        restartedLogic.execute("clear");
        restartedLogic.execute("yes");
        assertTrue(addressStorage.readAddressBook().orElseThrow().getFavouriteContacts().isEmpty());
        assertTrue(restarted.getFilteredPersonList().isEmpty());
    }
}
