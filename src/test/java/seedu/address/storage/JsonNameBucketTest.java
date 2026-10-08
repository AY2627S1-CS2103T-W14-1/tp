package seedu.address.storage;

import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

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
}
