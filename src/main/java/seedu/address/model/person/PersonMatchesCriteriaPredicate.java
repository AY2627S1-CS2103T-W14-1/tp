package seedu.address.model.person;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} matches all specified field criteria.
 * Matching is case-insensitive and checks for substrings.
 */
public class PersonMatchesCriteriaPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final List<String> phoneKeywords;
    private final List<String> emailKeywords;
    private final List<String> locationKeywords;
    private final List<String> tagKeywords;

    /**
     * Constructs a {@code PersonMatchesCriteriaPredicate}.
     * Null arguments are treated as empty lists (meaning no filter applied on that field).
     */
    public PersonMatchesCriteriaPredicate(List<String> nameKeywords, List<String> phoneKeywords,
                                          List<String> emailKeywords, List<String> locationKeywords,
                                          List<String> tagKeywords) {
        this.nameKeywords = nameKeywords == null ? Collections.emptyList() : List.copyOf(nameKeywords);
        this.phoneKeywords = phoneKeywords == null ? Collections.emptyList() : List.copyOf(phoneKeywords);
        this.emailKeywords = emailKeywords == null ? Collections.emptyList() : List.copyOf(emailKeywords);
        this.locationKeywords = locationKeywords == null ? Collections.emptyList() : List.copyOf(locationKeywords);
        this.tagKeywords = tagKeywords == null ? Collections.emptyList() : List.copyOf(tagKeywords);
    }

    @Override
    public boolean test(Person person) {
        boolean matchesName = nameKeywords.isEmpty()
                || nameKeywords.stream().anyMatch(keyword ->
                person.getDisplayName().toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesPhone = phoneKeywords.isEmpty()
                || phoneKeywords.stream().anyMatch(keyword ->
                person.getPhone().value.toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesEmail = emailKeywords.isEmpty()
                || emailKeywords.stream().anyMatch(keyword ->
                person.getEmail().value.toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesLocation = locationKeywords.isEmpty()
                || locationKeywords.stream().anyMatch(keyword ->
                person.getAddress().value.toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesTag = tagKeywords.isEmpty()
                || tagKeywords.stream().anyMatch(keyword ->
                person.getTags().stream().anyMatch(tag ->
                        tag.tagName.toLowerCase().contains(keyword.toLowerCase())));

        return matchesName && matchesPhone && matchesEmail && matchesLocation && matchesTag;
    }

    public List<String> getNameKeywords() {
        return nameKeywords;
    }

    public List<String> getPhoneKeywords() {
        return phoneKeywords;
    }

    public List<String> getEmailKeywords() {
        return emailKeywords;
    }

    public List<String> getLocationKeywords() {
        return locationKeywords;
    }

    public List<String> getTagKeywords() {
        return tagKeywords;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PersonMatchesCriteriaPredicate otherPredicate)) {
            return false;
        }

        return nameKeywords.equals(otherPredicate.nameKeywords)
                && phoneKeywords.equals(otherPredicate.phoneKeywords)
                && emailKeywords.equals(otherPredicate.emailKeywords)
                && locationKeywords.equals(otherPredicate.locationKeywords)
                && tagKeywords.equals(otherPredicate.tagKeywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKeywords, phoneKeywords, emailKeywords, locationKeywords, tagKeywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("phoneKeywords", phoneKeywords)
                .add("emailKeywords", emailKeywords)
                .add("locationKeywords", locationKeywords)
                .add("tagKeywords", tagKeywords)
                .toString();
    }
}
