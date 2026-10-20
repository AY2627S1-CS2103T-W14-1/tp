package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
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
import seedu.address.model.person.Role;
import seedu.address.model.person.UnindexedPerson;
import seedu.address.model.person.Year;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    public static final String NULL_TAG_MESSAGE = "Person tags must not contain null entries.";

    private final String name;
    private final String role;
    private final String phone;
    private final String email;
    private final String address;
    private final String major;
    private final String year;
    private final String faculty;
    private final String department;
    private final String hours;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String photo;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("role") String role,
            @JsonProperty("phone") String phone, @JsonProperty("email") String email,
            @JsonProperty("address") String address, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("major") String major, @JsonProperty("year") String year,
            @JsonProperty("faculty") String faculty, @JsonProperty("department") String department,
            @JsonProperty("hours") String hours, @JsonProperty("photo") String photo) {
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.major = major;
        this.year = year;
        this.faculty = faculty;
        this.department = department;
        this.hours = hours;
        this.photo = photo;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Constructs a person without a photo reference. Kept for existing tests and legacy JSON data.
     */
    public JsonAdaptedPerson(String name, String role, String phone, String email, String address,
                             List<JsonAdaptedTag> tags, String major, String year, String faculty, String department,
                             String hours) {
        this(name, role, phone, email, address, tags, major, year, faculty, department, hours, null);
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        role = source.getRole().role;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        major = source.getMajor() != null ? source.getMajor().value : null;
        year = source.getYear() != null ? source.getYear().value : null;
        faculty = source.getFaculty() != null ? source.getFaculty().value : null;
        department = source.getDepartment() != null ? source.getDepartment().value : null;
        hours = source.getHours() != null ? source.getHours().value : null;
        photo = source.getPhoto().map(Photo::getRelativePath).orElse(null);
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public UnindexedPerson toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException(NULL_TAG_MESSAGE);
            }
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (role == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Role.class.getSimpleName()));
        }
        if (!Role.isValidRole(role)) {
            throw new IllegalValueException(Role.MESSAGE_CONSTRAINTS);
        }
        final Role modelRole = new Role(role);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (!Major.isValidMajor(major)) {
            throw new IllegalValueException(Major.MESSAGE_CONSTRAINTS);
        }
        final Major modelMajor = new Major(major);

        if (!Year.isValidYear(year)) {
            throw new IllegalValueException(Year.MESSAGE_CONSTRAINTS);
        }
        final Year modelYear = new Year(year);

        if (!Faculty.isValidFaculty(faculty)) {
            throw new IllegalValueException(Faculty.MESSAGE_CONSTRAINTS);
        }
        final Faculty modelFaculty = new Faculty(faculty);

        if (!Department.isValidDepartment(department)) {
            throw new IllegalValueException(Department.MESSAGE_CONSTRAINTS);
        }
        final Department modelDepartment = new Department(department);

        if (!Hours.isValidHours(hours)) {
            throw new IllegalValueException(Hours.MESSAGE_CONSTRAINTS);
        }
        final Hours modelHours = new Hours(hours);

        final Set<Tag> modelTags = new HashSet<>(personTags);

        final Photo modelPhoto;

        try {
            modelPhoto = photo == null ? null : new Photo(photo);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage(), exception);
        }

        return new UnindexedPerson(modelName, modelRole, modelPhone, modelEmail, modelAddress, modelMajor, modelYear,
                                   modelFaculty, modelDepartment, modelHours, modelTags, modelPhoto);
    }

}
