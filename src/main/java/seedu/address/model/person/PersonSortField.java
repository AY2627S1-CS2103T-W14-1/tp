package seedu.address.model.person;

import java.util.Comparator;

/** Fields by which persons can be sorted. */
public enum PersonSortField {
    NAME("name", Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER)),
    PHONE("phone", Comparator.comparing(person -> person.getPhone().value)),
    ADD_TIME("add", Comparator.comparing(Person::getCreatedAt,
            Comparator.nullsFirst(Comparator.naturalOrder()))),
    EDIT_TIME("edit", Comparator.comparing(Person::getLastEditedAt,
            Comparator.nullsFirst(Comparator.naturalOrder())));

    private final String userInput;
    private final Comparator<Person> comparator;

    PersonSortField(String userInput, Comparator<Person> comparator) {
        this.userInput = userInput;
        this.comparator = comparator;
    }

    public Comparator<Person> getComparator() {
        return comparator;
    }

    /**
     * Returns the comparator for this field and direction.
     *
     * <p>Name sorting uses the person index as a tie-breaker so duplicate names
     * appear in reverse index order when sorting names descending. Other fields
     * retain their existing comparison behavior.</p>
     */
    public Comparator<Person> getComparator(boolean descending) {
        Comparator<Person> result = comparator;
        if (this == NAME) {
            result = result.thenComparingInt(person -> person.getPersonIndex().getZeroBased());
        }
        if (this == ADD_TIME || this == EDIT_TIME) {
            // For timestamps, ascending means most recent first. Descending is the reverse.
            return descending ? result : result.reversed();
        }
        return descending ? result.reversed() : result;
    }

    /** Returns the sort field represented by the given user input. */
    public static PersonSortField fromUserInput(String userInput) {
        for (PersonSortField field : values()) {
            if (field.userInput.equalsIgnoreCase(userInput)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Unsupported sort field: " + userInput);
    }

    @Override
    public String toString() {
        return userInput;
    }
}
