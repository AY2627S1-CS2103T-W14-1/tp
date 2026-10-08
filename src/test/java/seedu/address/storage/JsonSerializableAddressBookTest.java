package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_legacyDuplicatePersons_assignsDistinctIds() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBook = dataFromFile.toModelType();

        assertEquals(2, addressBook.getPersonList().size());
        assertEquals(0, addressBook.getPersonList().get(0).getPersonIndex().getZeroBased());
        assertEquals(1, addressBook.getPersonList().get(1).getPersonIndex().getZeroBased());
    }

    @Test
    public void toModelType_nestedBuckets_preservesSuffixes() throws Exception {
        Person alice = TypicalPersons.ALICE;
        Person thirdAlice = new PersonBuilder(alice).withPhone("99999999").build();
        AddressBook addressBook = new AddressBook();
        addressBook.setPersonBuckets(Map.of(alice.getName().fullName, Map.of(0, alice, 2, thirdAlice)));

        JsonSerializableAddressBook serializable = new JsonSerializableAddressBook(addressBook);
        AddressBook loaded = serializable.toModelType();

        assertEquals(addressBook.getPersonBuckets(), loaded.getPersonBuckets());
        assertTrue(JsonUtil.toJsonString(serializable).contains("personsByName"));
    }

}
