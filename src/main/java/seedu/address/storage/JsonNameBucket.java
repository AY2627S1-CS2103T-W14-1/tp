package seedu.address.storage;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Jackson-friendly representation of the suffix-keyed persons belonging to one base-name bucket.
 */
class JsonNameBucket {

    static final String MESSAGE_INVALID_SUFFIX = "Person suffixes must be non-negative integers.";
    static final String MESSAGE_NAME_MISMATCH = "Person name must match its name-bucket key.";

    private final Map<Integer, JsonAdaptedPerson> persons = new TreeMap<>();

    /**
     * Constructs a name bucket with the given suffix-keyed persons.
     */
    @JsonCreator
    JsonNameBucket(@JsonProperty("persons") Map<Integer, JsonAdaptedPerson> persons) {
        if (persons != null) {
            this.persons.putAll(persons);
        }
    }

    /**
     * Converts model persons to their Jackson-friendly representations.
     */
    static JsonNameBucket fromModel(Map<Integer, Person> persons) {
        Map<Integer, JsonAdaptedPerson> jsonPersons = new TreeMap<>();
        persons.forEach((suffix, person) -> jsonPersons.put(suffix, new JsonAdaptedPerson(person)));
        return new JsonNameBucket(jsonPersons);
    }

    /**
     * Converts this bucket into suffix-keyed model persons and validates its base name.
     */
    Map<Integer, Person> toModelType(String baseName) throws IllegalValueException {
        if (!Name.isValidName(baseName)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }

        Map<Integer, Person> modelPersons = new TreeMap<>();
        for (Map.Entry<Integer, JsonAdaptedPerson> jsonPerson : persons.entrySet()) {
            Integer suffix = jsonPerson.getKey();
            if (suffix == null || suffix < 0) {
                throw new IllegalValueException(MESSAGE_INVALID_SUFFIX);
            }

            Person person = jsonPerson.getValue().toModelType();
            if (!baseName.equals(person.getName().fullName)) {
                throw new IllegalValueException(MESSAGE_NAME_MISMATCH);
            }
            modelPersons.put(suffix, person);
        }
        return Collections.unmodifiableMap(modelPersons);
    }
}
