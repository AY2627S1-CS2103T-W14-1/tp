package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Photo;

class FavouriteStorageTest {
    @TempDir
    private Path tempDir;

    @Test
    void saveAndRead_favouriteWithPhoto_preservesAllDataAndSuffix() throws Exception {
        AddressBook original = new AddressBook();
        original.addPerson(ALICE);
        original.addPerson(ALICE.withPhoto(new Photo("photos/alice.png")));
        original.setFavourite(original.getPersonList().getLast(), true);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(tempDir.resolve("contacts.json"));
        storage.saveAddressBook(original);
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        assertEquals(original, new AddressBook(restored));
        assertEquals(new Photo("photos/alice.png"), restored.getPersonList().getLast().getPhoto().orElseThrow());
    }

    @Test
    void saveAndRead_favouritesAndRecentContacts_preservesBothStates() throws Exception {
        AddressBook original = new AddressBook();
        original.addPerson(ALICE);
        original.addPerson(BENSON);
        original.setFavourite(ALICE, true);
        original.recordPersonViewed(BENSON);
        original.recordPersonViewed(ALICE);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(tempDir.resolve("combined-contacts.json"));

        storage.saveAddressBook(original);
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();

        assertEquals(Set.of(ALICE.getDisplayName()), restored.getFavouriteContacts());
        assertEquals(List.of(ALICE.getDisplayName(), BENSON.getDisplayName()), restored.getRecentContacts());
    }

    @Test
    void toModel_legacyDataWithoutFavourites_defaultsToEmpty() throws Exception {
        JsonSerializableAddressBook legacy = new JsonSerializableAddressBook(null,
                List.of(new JsonAdaptedPerson(ALICE)), null, null);
        assertTrue(legacy.toModelType().getFavouriteContacts().isEmpty());
        assertEquals(List.of(ALICE), legacy.toModelType().getPersonList());
    }

    @Test
    void read_nullOrWrongShapeEntry_reportsDataError() {
        assertThrows(IOException.class, () -> JsonUtil.fromJsonString(
                "{\"favouriteContacts\":[null]}", JsonSerializableAddressBook.class));
        assertThrows(IOException.class, () -> JsonUtil.fromJsonString(
                "{\"favouriteContacts\":{\"name\":\"Alice\"}}", JsonSerializableAddressBook.class));
    }
}
