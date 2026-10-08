package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.PersonBuilder;

public class UniquePersonHashMapTest {

    private final UniquePersonHashMap persons = new UniquePersonHashMap();

    @Test
    public void add_sameNameWithDifferentDetails_storesBothPersonsWithSuffixes() {
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();

        persons.add(ALICE);
        persons.add(anotherAlice);

        assertEquals(List.of(ALICE, anotherAlice), persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, anotherAlice), persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void add_duplicateExactPerson_throwsDuplicatePersonException() {
        persons.add(ALICE);
        assertThrows(DuplicatePersonException.class, () -> persons.add(ALICE));
    }

    @Test
    public void remove_deletedSuffix_reusesSmallestAvailableSuffix() {
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        Person thirdAlice = new PersonBuilder(ALICE).withPhone("99999999").build();
        Person replacementAlice = new PersonBuilder(ALICE).withEmail("replacement@example.com").build();

        persons.add(ALICE);
        persons.add(anotherAlice);
        persons.add(thirdAlice);
        persons.remove(anotherAlice);
        persons.add(replacementAlice);

        assertEquals(Map.of(0, ALICE, 1, replacementAlice, 2, thirdAlice),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void remove_personNotPresent_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> persons.remove(ALICE));
    }

    @Test
    public void contains_sameNameWithDifferentDetails_returnsFalse() {
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();

        persons.add(ALICE);

        assertFalse(persons.contains(anotherAlice));
        assertTrue(persons.contains(ALICE));
    }

    @Test
    public void setPersons_preservesDisplayOrderAndBuildsBuckets() {
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();

        persons.setPersons(List.of(BOB, ALICE, anotherAlice));

        assertEquals(List.of(BOB, ALICE, anotherAlice), persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, anotherAlice), persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void setPersonBuckets_rebuildsSuffixAllocationState() {
        Person thirdAlice = new PersonBuilder(ALICE).withPhone("99999999").build();
        Person replacementAlice = new PersonBuilder(ALICE).withEmail("replacement@example.com").build();
        Map<String, Map<Integer, Person>> storedBuckets = Map.of(ALICE.getName().fullName,
                Map.of(0, ALICE, 2, thirdAlice));

        persons.setPersonBuckets(storedBuckets);
        persons.add(replacementAlice);

        assertEquals(List.of(ALICE, thirdAlice, replacementAlice), persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, replacementAlice, 2, thirdAlice),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void setPersonBuckets_nameDoesNotMatchBucketKey_throwsIllegalArgumentException() {
        Map<String, Map<Integer, Person>> storedBuckets = Map.of("Not Alice", Map.of(0, ALICE));

        assertThrows(IllegalArgumentException.class, () -> persons.setPersonBuckets(storedBuckets));
    }
}
