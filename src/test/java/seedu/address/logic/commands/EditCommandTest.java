package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Department;
import seedu.address.model.person.Faculty;
import seedu.address.model.person.Hours;
import seedu.address.model.person.Major;
import seedu.address.model.person.Person;
import seedu.address.model.person.Professor;
import seedu.address.model.person.Student;
import seedu.address.model.person.Year;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editPerson_preservesCreationTimeAndUpdatesEditTime() throws Exception {
        LocalDateTime creationTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person timestampedOriginal = original.withTimestamps(creationTime, creationTime)
                .withPersonIndex(original.getPersonIndex());
        model.setPerson(original, timestampedOriginal);

        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());
        editCommand.execute(model);

        Person edited = model.getFilteredPersonList().stream()
                .filter(person -> person.getName().fullName.equals(VALID_NAME_BOB))
                .findFirst()
                .orElseThrow();
        assertEquals(creationTime, edited.getCreatedAt());
        assertTrue(edited.getLastEditedAt().isAfter(creationTime));
    }

    @Test
    public void execute_editStudent_preservesStudentDetails() throws Exception {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Student student = new Student(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), new Major("Computer Science"), new Year("2"), original.getTags(),
                original.getPersonIndex(), original.getPhoto().orElse(null));
        model.setPerson(original, student);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        Person edited = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals("Computer Science", edited.getMajor().value);
        assertEquals("2", edited.getYear().value);
    }

    @Test
    public void execute_editProfessor_preservesProfessorDetails() throws Exception {
        Person original = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Professor professor = new Professor(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), new Faculty("Computing"), new Department("Software Engineering"),
                new Hours("9-5"), original.getTags(), original.getPersonIndex(), original.getPhoto().orElse(null));
        model.setPerson(original, professor);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        new EditCommand(INDEX_SECOND_PERSON, descriptor).execute(model);

        Person edited = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        assertEquals("Computing", edited.getFaculty().value);
        assertEquals("Software Engineering", edited.getDepartment().value);
        assertEquals("9-5", edited.getHours().value);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_renameToExistingName_assignsNextSuffix() throws Exception {
        Person target = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON,
                new EditPersonDescriptorBuilder().withName("Alice Pauline").build());

        CommandResult result = editCommand.execute(model);

        assertEquals(1, model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getDisplayName().equals("Alice Pauline_2")).count());
        assertTrue(result.getFeedbackToUser().contains("Alice Pauline_2"));
        assertFalse(model.getAddressBook().getPersonList().stream()
                .anyMatch(person -> person.isSamePerson(target)));
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
    }

    @Test
    public void execute_studentWithProfessorField_failure() {
        // First person is Alice (Student)
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withFaculty("Computing").build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_STUDENT_RESTRICTIONS);
    }

    @Test
    public void execute_professorWithStudentField_failure() {
        // Second person is Benson (Professor)
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withMajor("Computer Science").build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_PROFESSOR_RESTRICTIONS);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + ", photoSource=Optional.empty}";
        assertEquals(expected, editCommand.toString());
    }

}
