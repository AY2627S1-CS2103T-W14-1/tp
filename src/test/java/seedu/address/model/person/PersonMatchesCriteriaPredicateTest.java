package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PersonMatchesCriteriaPredicateTest {

    @Test
    public void test_nameMatches_returnsTrue() {
        // Case-insensitive & substring
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                List.of("ali"), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_multipleFieldsMatch_returnsTrue() {
        // Alice is at "Jurong" and has tag "friends"
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                List.of("Alice"), Collections.emptyList(), Collections.emptyList(),
                List.of("jurong"), List.of("friend"));
        assertTrue(predicate.test(ALICE));
    }

    @Test
    public void test_oneFieldFails_returnsFalse() {
        // Location does not match Alice
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                List.of("Alice"), Collections.emptyList(), Collections.emptyList(),
                List.of("COM1"), Collections.emptyList());
        assertFalse(predicate.test(ALICE));
    }

    @Test
    public void test_tagSearch_returnsTrue() {
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), List.of("owesMoney"));
        assertTrue(predicate.test(BENSON));
    }

    @Test
    public void test_roleMatches_returnsTrue() {
        // ALICE is a student
        PersonMatchesCriteriaPredicate studentPredicate = new PersonMatchesCriteriaPredicate(
                Collections.emptyList(), List.of("student"), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        assertTrue(studentPredicate.test(ALICE));

        // BENSON is a professor
        PersonMatchesCriteriaPredicate profPredicate = new PersonMatchesCriteriaPredicate(
                Collections.emptyList(), List.of("professor"), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        assertTrue(profPredicate.test(BENSON));
    }
}
