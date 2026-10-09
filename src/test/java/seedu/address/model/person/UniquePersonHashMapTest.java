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

import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.PersonBuilder;

public class UniquePersonHashMapTest {

    private final UniquePersonHashMap persons = new UniquePersonHashMap();

    private static Person withId(Person person, int id) {
        return person.withPersonIndex(seedu.address.commons.core.index.PersonIndex.fromZeroBased(id));
    }

    @Test
    public void add_sameNameWithDifferentDetails_storesBothPersonsWithSuffixes() {
        Person firstAlice = new PersonBuilder(ALICE).buildNoIndex();
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();

        persons.add(firstAlice);
        persons.add(anotherAlice);

        assertEquals(List.of(ALICE, withId(anotherAlice, 1)), persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, withId(anotherAlice, 1)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void add_duplicateExactPerson_assignsNextAvailableId() {
        Person firstAlice = new PersonBuilder(ALICE).buildNoIndex();
        Person secondAlice = new PersonBuilder(ALICE).buildNoIndex();

        persons.add(firstAlice);
        persons.add(secondAlice);

        assertEquals(Map.of(0, ALICE, 1, withId(secondAlice, 1)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void remove_deletedSuffix_reusesSmallestAvailableSuffix() {
        Person firstAlice = new PersonBuilder(ALICE).buildNoIndex();
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();
        Person thirdAlice = new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex();
        Person replacementAlice = new PersonBuilder(ALICE).withEmail("replacement@example.com").buildNoIndex();

        persons.add(firstAlice);
        persons.add(anotherAlice);
        persons.add(thirdAlice);
        persons.remove(withId(anotherAlice, 1));
        persons.add(replacementAlice);

        assertEquals(Map.of(0, ALICE, 1, withId(replacementAlice, 1), 2, withId(thirdAlice, 2)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void remove_personNotPresent_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> persons.remove(ALICE));
    }

    @Test
    public void contains_sameNameWithDifferentDetails_returnsFalse() {
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();

        persons.add(ALICE);

        assertFalse(persons.contains(anotherAlice));
        assertTrue(persons.contains(ALICE));
    }

    @Test
    public void setPersons_preservesDisplayOrderAndBuildsBuckets() {
        Person anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();

        persons.setPersons(List.of(BOB, ALICE, anotherAlice));

        assertEquals(List.of(ALICE, withId(anotherAlice, 1), BOB), persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, withId(anotherAlice, 1)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void setPersonBuckets_rebuildsSuffixAllocationState() {
        Person thirdAlice = new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex();
        Person replacementAlice = new PersonBuilder(ALICE).withEmail("replacement@example.com").buildNoIndex();
        Map<String, Map<Integer, Person>> storedBuckets = Map.of(ALICE.getName().fullName,
                Map.of(0, ALICE, 2, thirdAlice));

        persons.setPersonBuckets(storedBuckets);
        persons.add(replacementAlice);

        assertEquals(List.of(ALICE, withId(replacementAlice, 1), withId(thirdAlice, 2)),
                persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, withId(replacementAlice, 1), 2, withId(thirdAlice, 2)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void setPersonBuckets_nameDoesNotMatchBucketKey_throwsIllegalArgumentException() {
        Map<String, Map<Integer, Person>> storedBuckets = Map.of("Not Alice", Map.of(0, ALICE));

        assertThrows(IllegalArgumentException.class, () -> persons.setPersonBuckets(storedBuckets));
    }
}
