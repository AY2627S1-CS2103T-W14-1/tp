package seedu.address.storage;

import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonNameBucketTest {

    @Test
    public void toModelType_negativeSuffix_throwsIllegalValueException() {
        JsonNameBucket bucket = new JsonNameBucket(Map.of(-1, new JsonAdaptedPerson(ALICE)));

        assertThrows(IllegalValueException.class, JsonNameBucket.MESSAGE_INVALID_SUFFIX, () ->
                bucket.toModelType(ALICE.getName().fullName));
    }

    @Test
    public void toModelType_personNameDoesNotMatchBucketKey_throwsIllegalValueException() {
        JsonNameBucket bucket = new JsonNameBucket(Map.of(0, new JsonAdaptedPerson(ALICE)));

        assertThrows(IllegalValueException.class, JsonNameBucket.MESSAGE_NAME_MISMATCH, () ->
                bucket.toModelType("Benson Meier"));
    }

    @Test
    public void toModelType_nullPerson_throwsIllegalValueException() {
        Map<Integer, JsonAdaptedPerson> persons = new HashMap<>();
        persons.put(0, null);
        JsonNameBucket bucket = new JsonNameBucket(persons);

        assertThrows(IllegalValueException.class, JsonNameBucket.MESSAGE_NULL_PERSON, () ->
                bucket.toModelType(ALICE.getName().fullName));
    }
}
