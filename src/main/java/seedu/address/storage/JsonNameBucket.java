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
 * Jackson-friendly representation of the id-keyed persons belonging to one name bucket.
 */
class JsonNameBucket {

    static final String MESSAGE_INVALID_SUFFIX = "Person ids must be non-negative integers.";
    static final String MESSAGE_NAME_MISMATCH = "Person name must match its name-bucket key.";

    private final Map<Integer, JsonAdaptedPerson> persons = new TreeMap<>();

    /**
     * Constructs a name bucket with the given id-keyed persons.
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
        persons.forEach((id, person) -> {
            if (id == null || id < 0 || person == null || person.getPersonIndex() == null
                    || id != person.getPersonIndex().getZeroBased()) {
                throw new IllegalArgumentException("Person bucket ids must match PersonIndex values.");
            }
            jsonPersons.put(id, new JsonAdaptedPerson(person));
        });
        return new JsonNameBucket(jsonPersons);
    }

    /**
     * Converts this bucket into id-keyed model persons and validates its name.
     */
    Map<Integer, Person> toModelType(String baseName) throws IllegalValueException {
        if (!Name.isValidName(baseName)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }

        Map<Integer, Person> modelPersons = new TreeMap<>();
        for (Map.Entry<Integer, JsonAdaptedPerson> jsonPerson : persons.entrySet()) {
            Integer id = jsonPerson.getKey();
            if (id == null || id < 0) {
                throw new IllegalValueException(MESSAGE_INVALID_SUFFIX);
            }

            Person person = jsonPerson.getValue().toModelType();
            if (!baseName.equals(person.getName().fullName)) {
                throw new IllegalValueException(MESSAGE_NAME_MISMATCH);
            }
            modelPersons.put(id, person);
        }
        return Collections.unmodifiableMap(modelPersons);
    }
}
