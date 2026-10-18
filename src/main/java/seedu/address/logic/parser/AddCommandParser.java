package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_MAJOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_OFFICE_HOURS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_ROLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Department;
import seedu.address.model.person.Email;
import seedu.address.model.person.Faculty;
import seedu.address.model.person.Hours;
import seedu.address.model.person.Major;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Role;
import seedu.address.model.person.UnindexedPerson;
import seedu.address.model.person.Year;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_CONTACT_ROLE, PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_PHONE,
                        PREFIX_CONTACT_LOCATION, PREFIX_TAG, PREFIX_CONTACT_MAJOR, PREFIX_CONTACT_YEAR,
                        PREFIX_CONTACT_FACULTY, PREFIX_CONTACT_DEPARTMENT, PREFIX_CONTACT_OFFICE_HOURS);

        if (!arePrefixesPresent(argMultimap, PREFIX_CONTACT_ROLE, PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_PHONE,
                PREFIX_CONTACT_LOCATION)
                || argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CONTACT_ROLE, PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_PHONE,
                PREFIX_CONTACT_LOCATION, PREFIX_CONTACT_MAJOR, PREFIX_CONTACT_YEAR, PREFIX_CONTACT_FACULTY,
                PREFIX_CONTACT_DEPARTMENT, PREFIX_CONTACT_OFFICE_HOURS);
        Name name = ParserUtil.parseName(argMultimap.getPreamble());
        Role role = ParserUtil.parseRole(argMultimap.getValue(PREFIX_CONTACT_ROLE).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_CONTACT_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_CONTACT_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_CONTACT_LOCATION).get());
        Major major = argMultimap.getValue(PREFIX_CONTACT_MAJOR).isPresent()
                ? ParserUtil.parseMajor(argMultimap.getValue(PREFIX_CONTACT_MAJOR).get())
                : null;
        Year year = argMultimap.getValue(PREFIX_CONTACT_YEAR).isPresent()
                ? ParserUtil.parseYear(argMultimap.getValue(PREFIX_CONTACT_YEAR).get())
                : null;
        Faculty faculty = argMultimap.getValue(PREFIX_CONTACT_FACULTY).isPresent()
                ? ParserUtil.parseFaculty(argMultimap.getValue(PREFIX_CONTACT_FACULTY).get())
                : null;
        Department department = argMultimap.getValue(PREFIX_CONTACT_DEPARTMENT).isPresent()
                ? ParserUtil.parseDepartment(argMultimap.getValue(PREFIX_CONTACT_DEPARTMENT).get())
                : null;
        Hours hours = argMultimap.getValue(PREFIX_CONTACT_OFFICE_HOURS).isPresent()
                ? ParserUtil.parseHours(argMultimap.getValue(PREFIX_CONTACT_OFFICE_HOURS).get())
                : null;
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        if (role.equals(new Role("student")) && areAnyPrefixesPresent(argMultimap, PREFIX_CONTACT_FACULTY,
                PREFIX_CONTACT_DEPARTMENT, PREFIX_CONTACT_OFFICE_HOURS)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddCommand.INVALID_STUDENT_PARAMETERS));
        }
        if (role.equals(new Role("professor")) && areAnyPrefixesPresent(argMultimap, PREFIX_CONTACT_MAJOR,
                PREFIX_CONTACT_YEAR)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddCommand.INVALID_PROFESSOR_PARAMETERS));
        }
        UnindexedPerson person = new UnindexedPerson(name, role, phone, email, address, major, year, faculty,
                department, hours, tagList);

        return new AddCommand(person);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if at least one of the prefixes contains a value in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean areAnyPrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
