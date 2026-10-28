package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_MAJOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_OFFICE_HOURS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_ROLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.UnindexedPerson;
import seedu.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(UnindexedPerson person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(UnindexedPerson person) {
        StringBuilder sb = new StringBuilder();
        sb.append(person.getName().fullName + " ");
        sb.append(PREFIX_CONTACT_ROLE + " " + person.getRole().toString() + " ");
        sb.append(PREFIX_CONTACT_EMAIL + " " + person.getEmail().value + " ");
        sb.append(PREFIX_CONTACT_PHONE + " " + person.getPhone().value + " ");
        sb.append(PREFIX_CONTACT_LOCATION + " " + person.getAddress().value + " ");
        person.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name ->
                sb.append(PREFIX_CONTACT_NAME).append(" ").append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone ->
                sb.append(PREFIX_CONTACT_PHONE).append(" ").append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email ->
                sb.append(PREFIX_CONTACT_EMAIL).append(" ").append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_CONTACT_LOCATION).append(" ")
                .append(address.value).append(" "));
        descriptor.getMajor().ifPresent(major -> sb.append(PREFIX_CONTACT_MAJOR).append(" ")
                .append(major.value).append(" "));
        descriptor.getYear().ifPresent(year -> sb.append(PREFIX_CONTACT_YEAR).append(" ")
                .append(year.value).append(" "));
        descriptor.getFaculty().ifPresent(faculty -> sb.append(PREFIX_CONTACT_FACULTY).append(" ")
                .append(faculty.value).append(" "));
        descriptor.getDepartment().ifPresent(dept -> sb.append(PREFIX_CONTACT_DEPARTMENT).append(" ")
                .append(dept.value).append(" "));
        descriptor.getHours().ifPresent(hours -> sb.append(PREFIX_CONTACT_OFFICE_HOURS).append(" ")
                .append(hours.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
