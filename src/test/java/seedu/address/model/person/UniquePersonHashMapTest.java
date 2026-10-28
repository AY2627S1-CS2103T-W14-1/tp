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

    private static Person withId(UnindexedPerson person, int id) {
        return person.withPersonIndex(seedu.address.commons.core.index.PersonIndex.fromZeroBased(id));
    }

    @Test
    public void add_sameNameWithDifferentDetails_storesBothPersonsWithSuffixes() {
        UnindexedPerson firstAlice = new PersonBuilder(ALICE).buildNoIndex();
        UnindexedPerson anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();

        persons.add(firstAlice);
        persons.add(anotherAlice);

        assertEquals(List.of(ALICE, withId(anotherAlice, 1)), persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, withId(anotherAlice, 1)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void add_duplicateExactPerson_assignsNextAvailableId() {
        UnindexedPerson firstAlice = new PersonBuilder(ALICE).buildNoIndex();
        UnindexedPerson secondAlice = new PersonBuilder(ALICE).buildNoIndex();

        persons.add(firstAlice);
        persons.add(secondAlice);

        assertEquals(Map.of(0, ALICE, 1, withId(secondAlice, 1)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void remove_deletedSuffix_reusesSmallestAvailableSuffix() {
        UnindexedPerson firstAlice = new PersonBuilder(ALICE).buildNoIndex();
        UnindexedPerson anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();
        UnindexedPerson thirdAlice = new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex();
        UnindexedPerson replacementAlice = new PersonBuilder(ALICE).withEmail("replacement@example.com").buildNoIndex();

        persons.add(firstAlice);
        persons.add(anotherAlice);
        persons.add(thirdAlice);
        persons.remove(withId(anotherAlice, 1));
        persons.add(replacementAlice);

        assertEquals(Map.of(0, ALICE, 1, withId(replacementAlice, 1), 2, withId(thirdAlice, 2)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void setPerson_sameNameReplacementDoesNotFreeItsId() {
        UnindexedPerson anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();
        persons.add(ALICE);
        persons.add(anotherAlice);
        Person indexedSecondAlice = withId(anotherAlice, 1);
        Person editedSecondAlice = new PersonBuilder(indexedSecondAlice).withPhone("91234567").build();

        persons.setPerson(indexedSecondAlice, editedSecondAlice);
        UnindexedPerson thirdAlice = new PersonBuilder(ALICE).withEmail("third@example.com").buildNoIndex();
        persons.add(thirdAlice);

        assertEquals(List.of(ALICE, editedSecondAlice, withId(thirdAlice, 2)), persons.asUnmodifiableObservableList());
    }

    @Test
    public void setPerson_renameAllocatesSmallestIdInDestinationBucket() {
        persons.add(ALICE);
        UnindexedPerson anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();
        persons.add(anotherAlice);
        Person indexedSecondAlice = withId(anotherAlice, 1);
        Person renamedPerson = new PersonBuilder(indexedSecondAlice).withName("Bob Meier").build();

        Person storedRenamedPerson = persons.setPerson(indexedSecondAlice, renamedPerson);
        UnindexedPerson anotherBob = new PersonBuilder().withName("Bob Meier").withPhone("91234567")
                .withEmail("anotherbob@example.com").buildNoIndex();
        persons.add(anotherBob);

        assertEquals("Bob Meier", storedRenamedPerson.getDisplayName());
        assertEquals(0, storedRenamedPerson.getPersonIndex().getZeroBased());
        assertEquals(List.of(storedRenamedPerson, withId(anotherBob, 1)), persons.getPersonBuckets()
                .get("Bob Meier").values().stream().toList());
    }

    @Test
    public void setPerson_renameToExistingNameAllocatesNextId() {
        persons.add(ALICE);
        Person secondPerson = new PersonBuilder(BOB).build();
        persons.add(secondPerson);
        Person renamedPerson = new PersonBuilder(secondPerson).withName(ALICE.getName().fullName).build();

        Person storedRenamedPerson = persons.setPerson(secondPerson, renamedPerson);

        assertEquals(1, storedRenamedPerson.getPersonIndex().getZeroBased());
        assertEquals(List.of(ALICE, storedRenamedPerson), persons.getPersonBuckets()
                .get(ALICE.getName().fullName).values().stream().toList());
    }

    @Test
    public void remove_personNotPresent_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> persons.remove(ALICE));
    }

    @Test
    public void contains_sameNameWithDifferentDetails_returnsFalse() {
        UnindexedPerson anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();

        persons.add(ALICE);

        assertFalse(persons.contains(anotherAlice));
        assertTrue(persons.contains(ALICE));
    }

    @Test
    public void setPersons_preservesDisplayOrderAndBuildsBuckets() {
        UnindexedPerson anotherAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).buildNoIndex();

        persons.setPersons(List.of(BOB, ALICE, anotherAlice));

        assertEquals(List.of(ALICE, withId(anotherAlice, 1), BOB), persons.asUnmodifiableObservableList());
        assertEquals(Map.of(0, ALICE, 1, withId(anotherAlice, 1)),
                persons.getPersonBuckets().get(ALICE.getName().fullName));
    }

    @Test
    public void setPersonBuckets_rebuildsSuffixAllocationState() {
        UnindexedPerson thirdAlice = new PersonBuilder(ALICE).withPhone("99999999").buildNoIndex();
        UnindexedPerson replacementAlice = new PersonBuilder(ALICE).withEmail("replacement@example.com").buildNoIndex();
        Map<String, Map<Integer, Person>> storedBuckets = Map.of(ALICE.getName().fullName,
                Map.of(0, ALICE, 2, withId(thirdAlice, 2)));

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
