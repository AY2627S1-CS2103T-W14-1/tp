package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TAG;
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
                PREFIX_CONTACT_NAME, PREFIX_CONTACT_PHONE, PREFIX_CONTACT_EMAIL,
                PREFIX_CONTACT_LOCATION, PREFIX_TAG, PREFIX_CONTACT_TAG);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CONTACT_NAME, PREFIX_CONTACT_PHONE,
                PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_LOCATION);

        List<String> nameKeywords = new ArrayList<>();
        List<String> phoneKeywords = new ArrayList<>();
        List<String> emailKeywords = new ArrayList<>();
        List<String> locationKeywords = new ArrayList<>();
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

        if (nameKeywords.isEmpty() && phoneKeywords.isEmpty() && emailKeywords.isEmpty()
                && locationKeywords.isEmpty() && tagKeywords.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(new PersonMatchesCriteriaPredicate(nameKeywords, phoneKeywords,
                emailKeywords, locationKeywords, tagKeywords));
    }

}
