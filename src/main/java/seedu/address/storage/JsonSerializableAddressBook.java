package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.UnindexedPerson;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    static final String MESSAGE_NULL_NAME_BUCKET = "Name buckets must not be null.";
    static final String MESSAGE_NULL_PERSON = "Person entries must not be null.";

    private final Map<String, JsonNameBucket> personsByName = new TreeMap<>();
    private final List<String> recentContacts = new ArrayList<>();
    private final Set<String> favouriteContacts = new TreeSet<>();
    @JsonIgnore
    private final List<JsonAdaptedPerson> legacyPersons = new ArrayList<>();
    @JsonIgnore
    private final boolean hasNameBuckets;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with nested name buckets.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("personsByName") Map<String, JsonNameBucket> personsByName,
            @JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("recentContacts") List<String> recentContacts,
            @JsonProperty("favouriteContacts") Set<String> favouriteContacts) {
        hasNameBuckets = personsByName != null;
        if (personsByName != null) {
            this.personsByName.putAll(personsByName);
        }
        if (persons != null) {
            legacyPersons.addAll(persons);
        }
        if (recentContacts != null) {
            this.recentContacts.addAll(recentContacts);
        }
        if (favouriteContacts != null) {
            this.favouriteContacts.addAll(favouriteContacts);
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
        recentContacts.addAll(source.getRecentContacts());
        favouriteContacts.addAll(source.getFavouriteContacts());
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        if (recentContacts.contains(null)) {
            throw new IllegalValueException("Recent contact identifiers must not be null.");
        }
        if (hasNameBuckets) {
            Map<String, Map<Integer, Person>> modelBuckets = new TreeMap<>();
            for (Map.Entry<String, JsonNameBucket> jsonBucket : personsByName.entrySet()) {
                if (jsonBucket.getValue() == null) {
                    throw new IllegalValueException(MESSAGE_NULL_NAME_BUCKET);
                }
                modelBuckets.put(jsonBucket.getKey(), jsonBucket.getValue().toModelType(jsonBucket.getKey()));
            }
            try {
                addressBook.setPersonBuckets(modelBuckets);
            } catch (IllegalArgumentException exception) {
                throw new IllegalValueException(exception.getMessage(), exception);
            }
            addressBook.setRecentContacts(recentContacts);
            addressBook.setFavouriteContacts(favouriteContacts);
            return addressBook;
        }

        for (JsonAdaptedPerson jsonAdaptedPerson : legacyPersons) {
            if (jsonAdaptedPerson == null) {
                throw new IllegalValueException(MESSAGE_NULL_PERSON);
            }
            UnindexedPerson person = jsonAdaptedPerson.toModelType();
            addressBook.addPerson(person);
        }
        addressBook.setRecentContacts(recentContacts);
        addressBook.setFavouriteContacts(favouriteContacts);
        return addressBook;
    }

}
