package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_MAJOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_OFFICE_HOURS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_ROLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_CONTACT_NAME, PREFIX_CONTACT_ROLE, PREFIX_CONTACT_PHONE, PREFIX_CONTACT_EMAIL,
                PREFIX_CONTACT_LOCATION, PREFIX_CONTACT_MAJOR, PREFIX_CONTACT_YEAR,
                PREFIX_CONTACT_FACULTY, PREFIX_CONTACT_DEPARTMENT, PREFIX_CONTACT_OFFICE_HOURS,
                PREFIX_TAG, PREFIX_CONTACT_TAG);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CONTACT_NAME, PREFIX_CONTACT_ROLE,
                PREFIX_CONTACT_PHONE, PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_LOCATION,
                PREFIX_CONTACT_MAJOR, PREFIX_CONTACT_YEAR, PREFIX_CONTACT_FACULTY,
                PREFIX_CONTACT_DEPARTMENT, PREFIX_CONTACT_OFFICE_HOURS);

        List<String> nameKeywords = new ArrayList<>();
        List<String> roleKeywords = new ArrayList<>();
        List<String> phoneKeywords = new ArrayList<>();
        List<String> emailKeywords = new ArrayList<>();
        List<String> locationKeywords = new ArrayList<>();
        List<String> majorKeywords = new ArrayList<>();
        List<String> yearKeywords = new ArrayList<>();
        List<String> facultyKeywords = new ArrayList<>();
        List<String> departmentKeywords = new ArrayList<>();
        List<String> hoursKeywords = new ArrayList<>();
        List<String> tagKeywords = new ArrayList<>();

        String preamble = argMultimap.getPreamble().trim();
        boolean hasNamePrefix = argMultimap.getValue(PREFIX_CONTACT_NAME).isPresent();

        if (!preamble.isEmpty() && hasNamePrefix) {
            throw new ParseException("Please specify name either directly or using /name, not both.\n"
                    + FindCommand.MESSAGE_USAGE);
        }

        if (!preamble.isEmpty()) {
            nameKeywords.addAll(Arrays.asList(preamble.split("\\s+")));
        } else if (hasNamePrefix) {
            String nameArg = argMultimap.getValue(PREFIX_CONTACT_NAME).get().trim();
            if (nameArg.isEmpty()) {
                throw new ParseException("Name search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            nameKeywords.addAll(Arrays.asList(nameArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_ROLE).isPresent()) {
            String roleArg = argMultimap.getValue(PREFIX_CONTACT_ROLE).get().trim();
            if (roleArg.isEmpty()) {
                throw new ParseException("Role search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            roleKeywords.addAll(Arrays.asList(roleArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_PHONE).isPresent()) {
            String phoneArg = argMultimap.getValue(PREFIX_CONTACT_PHONE).get().trim();
            if (phoneArg.isEmpty()) {
                throw new ParseException("Phone search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            phoneKeywords.addAll(Arrays.asList(phoneArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_EMAIL).isPresent()) {
            String emailArg = argMultimap.getValue(PREFIX_CONTACT_EMAIL).get().trim();
            if (emailArg.isEmpty()) {
                throw new ParseException("Email search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            emailKeywords.addAll(Arrays.asList(emailArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_LOCATION).isPresent()) {
            String locArg = argMultimap.getValue(PREFIX_CONTACT_LOCATION).get().trim();
            if (locArg.isEmpty()) {
                throw new ParseException("Location search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            locationKeywords.addAll(Arrays.asList(locArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_MAJOR).isPresent()) {
            String majorArg = argMultimap.getValue(PREFIX_CONTACT_MAJOR).get().trim();
            if (majorArg.isEmpty()) {
                throw new ParseException("Major search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            majorKeywords.addAll(Arrays.asList(majorArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_YEAR).isPresent()) {
            String yearArg = argMultimap.getValue(PREFIX_CONTACT_YEAR).get().trim();
            if (yearArg.isEmpty()) {
                throw new ParseException("Year search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            yearKeywords.addAll(Arrays.asList(yearArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_FACULTY).isPresent()) {
            String facultyArg = argMultimap.getValue(PREFIX_CONTACT_FACULTY).get().trim();
            if (facultyArg.isEmpty()) {
                throw new ParseException("Faculty search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            facultyKeywords.addAll(Arrays.asList(facultyArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_DEPARTMENT).isPresent()) {
            String deptArg = argMultimap.getValue(PREFIX_CONTACT_DEPARTMENT).get().trim();
            if (deptArg.isEmpty()) {
                throw new ParseException("Department search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            departmentKeywords.addAll(Arrays.asList(deptArg.split("\\s+")));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_OFFICE_HOURS).isPresent()) {
            String hoursArg = argMultimap.getValue(PREFIX_CONTACT_OFFICE_HOURS).get().trim();
            if (hoursArg.isEmpty()) {
                throw new ParseException("Office hours search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            hoursKeywords.addAll(Arrays.asList(hoursArg.split("\\s+")));
        }

        // Support both t/ and /tag
        List<String> rawTagList = new ArrayList<>(argMultimap.getAllValues(PREFIX_TAG));
        rawTagList.addAll(argMultimap.getAllValues(PREFIX_CONTACT_TAG));
        for (String rawTag : rawTagList) {
            String trimmedTag = rawTag.trim();
            if (trimmedTag.isEmpty()) {
                throw new ParseException("Tag search keyword cannot be empty.\n" + FindCommand.MESSAGE_USAGE);
            }
            tagKeywords.addAll(Arrays.asList(trimmedTag.split("\\s+")));
        }

        if (nameKeywords.isEmpty() && roleKeywords.isEmpty() && phoneKeywords.isEmpty()
                && emailKeywords.isEmpty() && locationKeywords.isEmpty() && majorKeywords.isEmpty()
                && yearKeywords.isEmpty() && facultyKeywords.isEmpty() && departmentKeywords.isEmpty()
                && hoursKeywords.isEmpty() && tagKeywords.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(new PersonMatchesCriteriaPredicate(nameKeywords, roleKeywords,
                phoneKeywords, emailKeywords, locationKeywords, majorKeywords, yearKeywords,
                facultyKeywords, departmentKeywords, hoursKeywords, tagKeywords));
    }

}
