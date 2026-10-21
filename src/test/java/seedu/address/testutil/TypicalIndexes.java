package seedu.address.testutil;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.core.index.PersonIndex;

/**
 * A utility class containing typical index objects to be used in tests.
 */
public class TypicalIndexes {
    public static final Index INDEX_FIRST_PERSON = Index.fromOneBased(1);
    public static final Index INDEX_SECOND_PERSON = Index.fromOneBased(2);
    public static final Index INDEX_THIRD_PERSON = Index.fromOneBased(3);

    public static final PersonIndex PERSON_INDEX_FIRST = PersonIndex.fromOneBased(1);
    public static final PersonIndex PERSON_INDEX_SECOND = PersonIndex.fromOneBased(2);
    public static final PersonIndex PERSON_INDEX_THIRD = PersonIndex.fromOneBased(3);
}
