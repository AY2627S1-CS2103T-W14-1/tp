package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Photo;

class RecentStorageTest {
    @TempDir
    private Path tempDir;

    @Test
    void saveAndRead_duplicateNameWithPhoto_retainsOrderAndData() throws Exception {
        AddressBook original = new AddressBook();
        original.addPerson(ALICE);
        original.addPerson(ALICE.withPhoto(new Photo("photos/alice.png")));
        original.recordPersonViewed(ALICE);
        original.recordPersonViewed(original.getPersonList().getLast());
        JsonAddressBookStorage storage = new JsonAddressBookStorage(tempDir.resolve("contacts.json"));
        storage.saveAddressBook(original);
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        assertEquals(original, new AddressBook(restored));
        assertEquals(new Photo("photos/alice.png"), restored.getPersonList().getLast().getPhoto().orElseThrow());
    }

    @Test
    void read_oldDataWithoutHistory_defaultsToEmpty() throws Exception {
        JsonSerializableAddressBook legacy = new JsonSerializableAddressBook(null,
                List.of(new JsonAdaptedPerson(ALICE)), null);
        assertTrue(legacy.toModelType().getRecentContacts().isEmpty());
        assertEquals(List.of(ALICE), legacy.toModelType().getPersonList());
    }

    @Test
    void read_nullHistoryEntry_reportsInvalidData() throws Exception {
        JsonSerializableAddressBook data = JsonUtil.fromJsonString("{\"recentContacts\":[null]}",
                JsonSerializableAddressBook.class);
        assertThrows(IllegalValueException.class, data::toModelType);
    }
}
