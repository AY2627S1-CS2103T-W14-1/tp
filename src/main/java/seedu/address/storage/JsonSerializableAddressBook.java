package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final Map<String, JsonNameBucket> personsByName = new TreeMap<>();
    @JsonIgnore
    private final List<JsonAdaptedPerson> legacyPersons = new ArrayList<>();
    @JsonIgnore
    private final boolean hasNameBuckets;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with nested name buckets.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("personsByName") Map<String, JsonNameBucket> personsByName,
            @JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        hasNameBuckets = personsByName != null;
        if (personsByName != null) {
            this.personsByName.putAll(personsByName);
        }
        if (persons != null) {
            legacyPersons.addAll(persons);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        hasNameBuckets = true;
        source.getPersonBuckets().forEach((name, bucket) ->
                personsByName.put(name, JsonNameBucket.fromModel(bucket)));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        if (hasNameBuckets) {
            Map<String, Map<Integer, Person>> modelBuckets = new TreeMap<>();
            for (Map.Entry<String, JsonNameBucket> jsonBucket : personsByName.entrySet()) {
                modelBuckets.put(jsonBucket.getKey(), jsonBucket.getValue().toModelType(jsonBucket.getKey()));
            }
            try {
                addressBook.setPersonBuckets(modelBuckets);
            } catch (IllegalArgumentException | DuplicatePersonException exception) {
                throw new IllegalValueException(exception.getMessage(), exception);
            }
            return addressBook;
        }

        for (JsonAdaptedPerson jsonAdaptedPerson : legacyPersons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        return addressBook;
    }

}
