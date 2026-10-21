package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Department;
import seedu.address.model.person.Email;
import seedu.address.model.person.Faculty;
import seedu.address.model.person.Hours;
import seedu.address.model.person.Major;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Professor;
import seedu.address.model.person.Role;
import seedu.address.model.person.Student;
import seedu.address.model.person.Year;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[/name NAME] "
            + "[/phone PHONE] "
            + "[/email EMAIL] "
            + "[/location LOCATION] "
            + "[/major MAJOR] "
            + "[/year YEAR] "
            + "[/faculty FACULTY] "
            + "[/department DEPARTMENT] "
            + "[/hours HOURS] "
            + "[t/TAG or /tag TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "/phone 91234567 "
            + "/email johndoe@example.com "
            + "/major Computer Science";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_STUDENT_RESTRICTIONS =
            "Student contacts cannot have faculty, department, or office hours.";
    public static final String MESSAGE_PROFESSOR_RESTRICTIONS =
            "Professor contacts cannot have major or year.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        assert personToEdit != null : "personToEdit retrieved from model list cannot be null";

        if (personToEdit.getRole().equals(new Role("student"))) {
            if (editPersonDescriptor.getFaculty().isPresent()
                    || editPersonDescriptor.getDepartment().isPresent()
                    || editPersonDescriptor.getHours().isPresent()) {
                throw new CommandException(MESSAGE_STUDENT_RESTRICTIONS);
            }
        } else if (personToEdit.getRole().equals(new Role("professor"))) {
            if (editPersonDescriptor.getMajor().isPresent()
                    || editPersonDescriptor.getYear().isPresent()) {
                throw new CommandException(MESSAGE_PROFESSOR_RESTRICTIONS);
            }
        }

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null : "personToEdit cannot be null";
        assert editPersonDescriptor != null : "editPersonDescriptor cannot be null";
        assert personToEdit.getRole() != null : "personToEdit must have a valid role";

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        // PersonIndex cannot be changed without a deletion

        if (personToEdit.getRole().equals(new Role("student"))) {
            Major updatedMajor = editPersonDescriptor.getMajor().orElse(personToEdit.getMajor());
            Year updatedYear = editPersonDescriptor.getYear().orElse(personToEdit.getYear());
            return new Student(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedMajor, updatedYear,
                    updatedTags, personToEdit.getPersonIndex(), personToEdit.getPhoto().orElse(null));
        } else if (personToEdit.getRole().equals(new Role("professor"))) {
            Faculty updatedFaculty = editPersonDescriptor.getFaculty().orElse(personToEdit.getFaculty());
            Department updatedDepartment = editPersonDescriptor.getDepartment().orElse(personToEdit.getDepartment());
            Hours updatedHours = editPersonDescriptor.getHours().orElse(personToEdit.getHours());
            return new Professor(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedFaculty,
                    updatedDepartment, updatedHours, updatedTags, personToEdit.getPersonIndex(),
                    personToEdit.getPhoto().orElse(null));
        } else {
            throw new IllegalArgumentException("Invalid person");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Major major;
        private Year year;
        private Faculty faculty;
        private Department department;
        private Hours hours;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setMajor(toCopy.major);
            setYear(toCopy.year);
            setFaculty(toCopy.faculty);
            setDepartment(toCopy.department);
            setHours(toCopy.hours);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, major, year, faculty, department, hours,
                    tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setMajor(Major major) {
            this.major = (major != null && major.value != null) ? major : null;
        }

        public Optional<Major> getMajor() {
            return Optional.ofNullable(major);
        }

        public void setYear(Year year) {
            this.year = (year != null && year.value != null) ? year : null;
        }

        public Optional<Year> getYear() {
            return Optional.ofNullable(year);
        }

        public void setFaculty(Faculty faculty) {
            this.faculty = (faculty != null && faculty.value != null) ? faculty : null;
        }

        public Optional<Faculty> getFaculty() {
            return Optional.ofNullable(faculty);
        }

        public void setDepartment(Department department) {
            this.department = (department != null && department.value != null) ? department : null;
        }

        public Optional<Department> getDepartment() {
            return Optional.ofNullable(department);
        }

        public void setHours(Hours hours) {
            this.hours = (hours != null && hours.value != null) ? hours : null;
        }

        public Optional<Hours> getHours() {
            return Optional.ofNullable(hours);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(major, otherEditPersonDescriptor.major)
                    && Objects.equals(year, otherEditPersonDescriptor.year)
                    && Objects.equals(faculty, otherEditPersonDescriptor.faculty)
                    && Objects.equals(department, otherEditPersonDescriptor.department)
                    && Objects.equals(hours, otherEditPersonDescriptor.hours)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("major", major)
                    .add("year", year)
                    .add("faculty", faculty)
                    .add("department", department)
                    .add("hours", hours)
                    .add("tags", tags)
                    .toString();
        }
    }
}
