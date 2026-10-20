package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_CONTACT_NAME, PREFIX_NAME,
                PREFIX_CONTACT_PHONE, PREFIX_PHONE,
                PREFIX_CONTACT_EMAIL, PREFIX_EMAIL,
                PREFIX_CONTACT_LOCATION, PREFIX_ADDRESS,
                PREFIX_CONTACT_TAG, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_CONTACT_NAME, PREFIX_NAME,
                PREFIX_CONTACT_PHONE, PREFIX_PHONE,
                PREFIX_CONTACT_EMAIL, PREFIX_EMAIL,
                PREFIX_CONTACT_LOCATION, PREFIX_ADDRESS);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        // Parse Name (supports /name or n/)
        Optional<String> nameValue = argMultimap.getValue(PREFIX_CONTACT_NAME)
                .or(() -> argMultimap.getValue(PREFIX_NAME));
        if (nameValue.isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(nameValue.get()));
        }

        // Parse Phone (supports /phone or p/)
        Optional<String> phoneValue = argMultimap.getValue(PREFIX_CONTACT_PHONE)
                .or(() -> argMultimap.getValue(PREFIX_PHONE));
        if (phoneValue.isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(phoneValue.get()));
        }

        // Parse Email (supports /email or e/)
        Optional<String> emailValue = argMultimap.getValue(PREFIX_CONTACT_EMAIL)
                .or(() -> argMultimap.getValue(PREFIX_EMAIL));
        if (emailValue.isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(emailValue.get()));
        }

        // Parse Location / Address (supports /location or a/)
        Optional<String> locationValue = argMultimap.getValue(PREFIX_CONTACT_LOCATION)
                .or(() -> argMultimap.getValue(PREFIX_ADDRESS));
        if (locationValue.isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(locationValue.get()));
        }

        // Parse Tags (supports /tag or t/)
        List<String> rawTags = new ArrayList<>(argMultimap.getAllValues(PREFIX_TAG));
        rawTags.addAll(argMultimap.getAllValues(PREFIX_CONTACT_TAG));
        parseTagsForEdit(rawTags).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
