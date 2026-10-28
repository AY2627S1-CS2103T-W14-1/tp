package seedu.address;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.storage.DataFileLock;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;

public class MainAppTest {
    @TempDir
    public Path testFolder;

    @Test
    public void init_secondInstance_cannotLoadOrOverwriteFirstInstancesContacts() throws Exception {
        Path dataFile = testFolder.resolve("data/contacts.json");
        Path prefsFile = testFolder.resolve("preferences.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(dataFile);
        storage.saveAddressBook(new AddressBook());
        MainApp first = new MainApp(dataFile, prefsFile);
        MainApp second = new MainApp(dataFile, prefsFile);
        try {
            first.init();
            second.init();
            assertNull(second.model);
            assertNull(second.logic);
            assertNull(second.ui);

            first.logic.execute("add Alice /role student /phone 12345678"
                    + " /email alice@example.com /location COM1");
            byte[] contactsBeforeStop = Files.readAllBytes(dataFile);
            byte[] prefsBeforeStop = Files.readAllBytes(prefsFile);
            second.stop();
            assertArrayEquals(contactsBeforeStop, Files.readAllBytes(dataFile));
            assertArrayEquals(prefsBeforeStop, Files.readAllBytes(prefsFile));
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            first.logic.execute("list");
        } finally {
            second.stop();
            first.stop();
        }

        MainApp reopened = new MainApp(dataFile, prefsFile);
        try {
            reopened.init();
            assertEquals("Alice", reopened.model.getAddressBook().getPersonList().getFirst().getDisplayName());
            assertEquals(1, reopened.model.getAddressBook().getPersonList().size());
        } finally {
            reopened.stop();
        }
    }

    @Test
    public void init_lockUnavailable_doesNotCreatePreferences() throws Exception {
        Path dataFile = testFolder.resolve("data/contacts.json");
        Path prefsFile = testFolder.resolve("preferences.json");
        try (DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            MainApp app = new MainApp(dataFile, prefsFile);
            app.init();
            app.stop();
            assertFalse(Files.exists(prefsFile));
            assertFalse(Files.exists(dataFile));
            assertNull(app.storage);
        }
    }

    @Test
    public void init_unwritableDataPath_doesNotLoadOrWritePreferences() throws Exception {
        Path invalidParent = testFolder.resolve("not-a-folder");
        Files.writeString(invalidParent, "keep me");
        Path prefsFile = testFolder.resolve("preferences.json");
        MainApp app = new MainApp(invalidParent.resolve("contacts.json"), prefsFile);
        app.init();
        app.stop();
        assertNull(app.model);
        assertFalse(Files.exists(prefsFile));
        assertEquals("keep me", Files.readString(invalidParent));
    }

    @Test
    public void init_failureAfterAcquiringLock_releasesLock() throws Exception {
        Path dataFile = testFolder.resolve("contacts.json");
        MainApp app = new MainApp(dataFile, testFolder.resolve("preferences.json")) {
            @Override
            protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
                throw new IllegalStateException("Simulated initialization failure");
            }
        };
        assertThrows(IllegalStateException.class, app::init);
        try (DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }
    }

    @Test
    public void stop_preferencesSaveFails_stillReleasesLock() throws Exception {
        Path dataFile = testFolder.resolve("contacts.json");
        Path prefsFile = testFolder.resolve("preferences.json");
        MainApp app = new MainApp(dataFile, prefsFile);
        try {
            app.init();
            Files.delete(prefsFile);
            Files.createDirectory(prefsFile);
        } finally {
            app.stop();
        }
        try (DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
        }
    }

    @Test
    public void start_uiFails_releasesLock() throws Exception {
        Path dataFile = testFolder.resolve("contacts.json");
        MainApp app = new MainApp(dataFile, testFolder.resolve("preferences.json"));
        try {
            app.init();
            app.ui = stage -> {
                throw new IllegalStateException("Simulated UI startup failure");
            };
            assertThrows(IllegalStateException.class, () -> app.start(null));
            try (DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
                assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            }
        } finally {
            app.stop();
        }
    }
}
