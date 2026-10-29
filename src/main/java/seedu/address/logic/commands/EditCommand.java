package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.core.index.PersonIndex;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.StringUtil;
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
import seedu.address.model.person.Photo;
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
            + "by name and an optional index for duplicate names. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: NAME[_PERSON_INDEX] "
            + "[/name NAME] "
            + "[/phone PHONE] "
            + "[/email EMAIL] "
            + "[/location LOCATION] "
            + "[/major MAJOR] "
            + "[/year YEAR] "
            + "[/faculty FACULTY] "
            + "[/department DEPARTMENT] "
            + "[/hours HOURS] "
            + "[/photo FILE_PATH or /photo [blank]] "
            + "[t/TAG or /tag TAG]...\n"
            + "Example: " + COMMAND_WORD + " John Doe_2 "
            + "/phone 91234567 "
            + "/email johndoe@example.com "
            + "/major Computer Science";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_STUDENT_RESTRICTIONS =
            "Student contacts cannot have faculty, department, or office hours.";
    public static final String MESSAGE_PROFESSOR_RESTRICTIONS =
            "Professor contacts cannot have major or year.";

    private final Index index;
    private final String targetName;
    private final PersonIndex targetPersonIndex;
    private final EditPersonDescriptor editPersonDescriptor;
    private final Optional<Path> photoSource;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        this(index, editPersonDescriptor, Optional.empty());
    }

    /** Creates an edit command with an optional source image to import before execution. */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor, Optional<Path> photoSource) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);
        requireNonNull(photoSource);

        this.index = index;
        this.targetName = null;
        this.targetPersonIndex = null;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
        this.photoSource = photoSource;
    }

    /** Creates an edit command targeting a contact by name and optional duplicate-name index. */
    public EditCommand(String targetName, PersonIndex targetPersonIndex,
                       EditPersonDescriptor editPersonDescriptor, Optional<Path> photoSource) {
        requireNonNull(targetName);
        requireNonNull(targetPersonIndex);
        requireNonNull(editPersonDescriptor);
        requireNonNull(photoSource);

        this.index = null;
        this.targetName = StringUtil.toProperCase(targetName);
        this.targetPersonIndex = targetPersonIndex;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
        this.photoSource = photoSource;
    }

    /** Returns the optional image path supplied by the user. */
    public Optional<Path> getPhotoSource() {
        return photoSource;
    }

    /** Returns a command copy whose contact references the imported image. */
    public EditCommand withPhoto(Photo photo) {
        requireNonNull(photo);
        EditPersonDescriptor updatedDescriptor = new EditPersonDescriptor(editPersonDescriptor);
        updatedDescriptor.setPhoto(photo);
        return index != null
                ? new EditCommand(index, updatedDescriptor)
                : new EditCommand(targetName, targetPersonIndex, updatedDescriptor, Optional.empty());
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToEdit = index == null
                ? model.getAddressBook().getPersonList().stream()
                        .filter(person -> person.getName().fullName.equals(targetName))
                        .filter(person -> person.getPersonIndex().equals(targetPersonIndex))
                        .findFirst()
                        .orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME))
                : getPersonFromDisplayedList(model);
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

        Person storedPerson = model.setPerson(personToEdit, editedPerson);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(storedPerson)));
    }

    private Person getPersonFromDisplayedList(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_NAME);
        }
        return lastShownList.get(index.getZeroBased());
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
        Photo updatedPhoto = editPersonDescriptor.isPhotoEdited()
                ? editPersonDescriptor.getPhoto().orElse(null) : personToEdit.getPhoto().orElse(null);

        if (personToEdit.getRole().equals(new Role("student"))) {
            Major updatedMajor = editPersonDescriptor.getMajor().orElse(personToEdit.getMajor());
            Year updatedYear = editPersonDescriptor.getYear().orElse(personToEdit.getYear());
            return new Student(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedMajor, updatedYear,
                    updatedTags, personToEdit.getPersonIndex(), updatedPhoto,
                    personToEdit.getCreatedAt(), LocalDateTime.now());
        } else if (personToEdit.getRole().equals(new Role("professor"))) {
            Faculty updatedFaculty = editPersonDescriptor.getFaculty().orElse(personToEdit.getFaculty());
            Department updatedDepartment = editPersonDescriptor.getDepartment().orElse(personToEdit.getDepartment());
            Hours updatedHours = editPersonDescriptor.getHours().orElse(personToEdit.getHours());
            return new Professor(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedFaculty,
                    updatedDepartment, updatedHours, updatedTags, personToEdit.getPersonIndex(),
                    updatedPhoto, personToEdit.getCreatedAt(), LocalDateTime.now());
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

        return java.util.Objects.equals(index, otherEditCommand.index)
                && java.util.Objects.equals(targetName, otherEditCommand.targetName)
                && java.util.Objects.equals(targetPersonIndex, otherEditCommand.targetPersonIndex)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor)
                && photoSource.equals(otherEditCommand.photoSource);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("targetName", targetName)
                .add("targetPersonIndex", targetPersonIndex)
                .add("editPersonDescriptor", editPersonDescriptor)
                .add("photoSource", photoSource)
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
        private Photo photo;
        private boolean photoEdited;

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
            if (toCopy.photoEdited) {
                setPhoto(toCopy.photo);
            }
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, major, year, faculty, department, hours,
                    tags) || photoEdited;
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
            this.major = major;
        }

        public Optional<Major> getMajor() {
            return Optional.ofNullable(major);
        }

        public void setYear(Year year) {
            this.year = year;
        }

        public Optional<Year> getYear() {
            return Optional.ofNullable(year);
        }

        public void setFaculty(Faculty faculty) {
            this.faculty = faculty;
        }

        public Optional<Faculty> getFaculty() {
            return Optional.ofNullable(faculty);
        }

        public void setDepartment(Department department) {
            this.department = department;
        }

        public Optional<Department> getDepartment() {
            return Optional.ofNullable(department);
        }

        public void setHours(Hours hours) {
            this.hours = hours;
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

        /** Sets the photo reference; null explicitly removes the current photo. */
        public void setPhoto(Photo photo) {
            this.photo = photo;
            photoEdited = true;
        }

        public Optional<Photo> getPhoto() {
            return Optional.ofNullable(photo);
        }

        public boolean isPhotoEdited() {
            return photoEdited;
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
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(photo, otherEditPersonDescriptor.photo)
                    && photoEdited == otherEditPersonDescriptor.photoEdited;
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
                    .add("photo", photo)
                    .add("photoEdited", photoEdited)
                    .toString();
        }
    }
}
