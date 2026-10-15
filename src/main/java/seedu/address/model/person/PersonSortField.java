package seedu.address.model.person;

import java.util.Comparator;

/** Fields by which persons can be sorted. */
public enum PersonSortField {
    NAME("name", Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER)),
    PHONE("phone", Comparator.comparing(person -> person.getPhone().value));

    private final String userInput;
    private final Comparator<Person> comparator;

    PersonSortField(String userInput, Comparator<Person> comparator) {
        this.userInput = userInput;
        this.comparator = comparator;
    }

    public Comparator<Person> getComparator() {
        return comparator;
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
