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
    private final List<String> roleKeywords;
    private final List<String> phoneKeywords;
    private final List<String> emailKeywords;
    private final List<String> locationKeywords;
    private final List<String> majorKeywords;
    private final List<String> yearKeywords;
    private final List<String> facultyKeywords;
    private final List<String> departmentKeywords;
    private final List<String> hoursKeywords;
    private final List<String> tagKeywords;

    /**
     * Constructs a {@code PersonMatchesCriteriaPredicate}.
     * Null arguments are treated as empty lists (meaning no filter applied on that field).
     */
    public PersonMatchesCriteriaPredicate(List<String> nameKeywords, List<String> roleKeywords,
                                          List<String> phoneKeywords, List<String> emailKeywords,
                                          List<String> locationKeywords, List<String> majorKeywords,
                                          List<String> yearKeywords, List<String> facultyKeywords,
                                          List<String> departmentKeywords, List<String> hoursKeywords,
                                          List<String> tagKeywords) {
        this.nameKeywords = nameKeywords == null ? Collections.emptyList() : List.copyOf(nameKeywords);
        this.roleKeywords = roleKeywords == null ? Collections.emptyList() : List.copyOf(roleKeywords);
        this.phoneKeywords = phoneKeywords == null ? Collections.emptyList() : List.copyOf(phoneKeywords);
        this.emailKeywords = emailKeywords == null ? Collections.emptyList() : List.copyOf(emailKeywords);
        this.locationKeywords = locationKeywords == null ? Collections.emptyList() : List.copyOf(locationKeywords);
        this.majorKeywords = majorKeywords == null ? Collections.emptyList() : List.copyOf(majorKeywords);
        this.yearKeywords = yearKeywords == null ? Collections.emptyList() : List.copyOf(yearKeywords);
        this.facultyKeywords = facultyKeywords == null ? Collections.emptyList() : List.copyOf(facultyKeywords);
        this.departmentKeywords = departmentKeywords == null ? Collections.emptyList()
                : List.copyOf(departmentKeywords);
        this.hoursKeywords = hoursKeywords == null ? Collections.emptyList() : List.copyOf(hoursKeywords);
        this.tagKeywords = tagKeywords == null ? Collections.emptyList() : List.copyOf(tagKeywords);
    }

    /**
     * Backwards-compatible constructor for basic 5 fields.
     */
    public PersonMatchesCriteriaPredicate(List<String> nameKeywords, List<String> phoneKeywords,
                                          List<String> emailKeywords, List<String> locationKeywords,
                                          List<String> tagKeywords) {
        this(nameKeywords, Collections.emptyList(), phoneKeywords, emailKeywords, locationKeywords,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), tagKeywords);
    }

    @Override
    public boolean test(Person person) {
        boolean matchesName = nameKeywords.isEmpty()
                || nameKeywords.stream().anyMatch(keyword ->
                person.getDisplayName().toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesRole = roleKeywords.isEmpty()
                || roleKeywords.stream().anyMatch(keyword ->
                person.getRole().role.toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesPhone = phoneKeywords.isEmpty()
                || phoneKeywords.stream().anyMatch(keyword ->
                person.getPhone().value.toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesEmail = emailKeywords.isEmpty()
                || emailKeywords.stream().anyMatch(keyword ->
                person.getEmail().value.toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesLocation = locationKeywords.isEmpty()
                || locationKeywords.stream().anyMatch(keyword ->
                person.getAddress().value.toLowerCase().contains(keyword.toLowerCase()));

        boolean matchesMajor = majorKeywords.isEmpty()
                || (person.getMajor().value != null && majorKeywords.stream().anyMatch(keyword ->
                person.getMajor().value.toLowerCase().contains(keyword.toLowerCase())));

        boolean matchesYear = yearKeywords.isEmpty()
                || (person.getYear().value != null && yearKeywords.stream().anyMatch(keyword ->
                person.getYear().value.toLowerCase().contains(keyword.toLowerCase())));

        boolean matchesFaculty = facultyKeywords.isEmpty()
                || (person.getFaculty().value != null && facultyKeywords.stream().anyMatch(keyword ->
                person.getFaculty().value.toLowerCase().contains(keyword.toLowerCase())));

        boolean matchesDepartment = departmentKeywords.isEmpty()
                || (person.getDepartment().value != null && departmentKeywords.stream().anyMatch(keyword ->
                person.getDepartment().value.toLowerCase().contains(keyword.toLowerCase())));

        boolean matchesHours = hoursKeywords.isEmpty()
                || (person.getHours().value != null && hoursKeywords.stream().anyMatch(keyword ->
                person.getHours().value.toLowerCase().contains(keyword.toLowerCase())));

        boolean matchesTag = tagKeywords.isEmpty()
                || tagKeywords.stream().anyMatch(keyword ->
                person.getTags().stream().anyMatch(tag ->
                        tag.tagName.toLowerCase().contains(keyword.toLowerCase())));

        return matchesName && matchesRole && matchesPhone && matchesEmail && matchesLocation
                && matchesMajor && matchesYear && matchesFaculty && matchesDepartment
                && matchesHours && matchesTag;
    }

    public List<String> getNameKeywords() {
        return nameKeywords;
    }

    public List<String> getRoleKeywords() {
        return roleKeywords;
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

    public List<String> getMajorKeywords() {
        return majorKeywords;
    }

    public List<String> getYearKeywords() {
        return yearKeywords;
    }

    public List<String> getFacultyKeywords() {
        return facultyKeywords;
    }

    public List<String> getDepartmentKeywords() {
        return departmentKeywords;
    }

    public List<String> getHoursKeywords() {
        return hoursKeywords;
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
                && roleKeywords.equals(otherPredicate.roleKeywords)
                && phoneKeywords.equals(otherPredicate.phoneKeywords)
                && emailKeywords.equals(otherPredicate.emailKeywords)
                && locationKeywords.equals(otherPredicate.locationKeywords)
                && majorKeywords.equals(otherPredicate.majorKeywords)
                && yearKeywords.equals(otherPredicate.yearKeywords)
                && facultyKeywords.equals(otherPredicate.facultyKeywords)
                && departmentKeywords.equals(otherPredicate.departmentKeywords)
                && hoursKeywords.equals(otherPredicate.hoursKeywords)
                && tagKeywords.equals(otherPredicate.tagKeywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKeywords, roleKeywords, phoneKeywords, emailKeywords, locationKeywords,
                majorKeywords, yearKeywords, facultyKeywords, departmentKeywords, hoursKeywords, tagKeywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("roleKeywords", roleKeywords)
                .add("phoneKeywords", phoneKeywords)
                .add("emailKeywords", emailKeywords)
                .add("locationKeywords", locationKeywords)
                .add("majorKeywords", majorKeywords)
                .add("yearKeywords", yearKeywords)
                .add("facultyKeywords", facultyKeywords)
                .add("departmentKeywords", departmentKeywords)
                .add("hoursKeywords", hoursKeywords)
                .add("tagKeywords", tagKeywords)
                .toString();
    }
}
