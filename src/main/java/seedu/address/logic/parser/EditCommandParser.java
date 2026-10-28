package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_DEPARTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_FACULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_MAJOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_OFFICE_HOURS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHOTO;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.nio.file.Path;
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
                PREFIX_CONTACT_NAME, PREFIX_CONTACT_PHONE,
                PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_LOCATION,
                PREFIX_CONTACT_MAJOR, PREFIX_CONTACT_YEAR,
                PREFIX_CONTACT_FACULTY, PREFIX_CONTACT_DEPARTMENT, PREFIX_CONTACT_OFFICE_HOURS,
                PREFIX_CONTACT_PHOTO, PREFIX_CONTACT_TAG, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_CONTACT_NAME, PREFIX_CONTACT_PHONE,
                PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_LOCATION,
                PREFIX_CONTACT_MAJOR, PREFIX_CONTACT_YEAR,
                PREFIX_CONTACT_FACULTY, PREFIX_CONTACT_DEPARTMENT, PREFIX_CONTACT_OFFICE_HOURS,
                PREFIX_CONTACT_PHOTO);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_CONTACT_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_CONTACT_NAME).get()));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_CONTACT_PHONE).get()));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_CONTACT_EMAIL).get()));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_LOCATION).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(
                    argMultimap.getValue(PREFIX_CONTACT_LOCATION).get()));
        }

        // Parse Student fields
        if (argMultimap.getValue(PREFIX_CONTACT_MAJOR).isPresent()) {
            editPersonDescriptor.setMajor(ParserUtil.parseMajor(argMultimap.getValue(PREFIX_CONTACT_MAJOR).get()));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_YEAR).isPresent()) {
            editPersonDescriptor.setYear(ParserUtil.parseYear(argMultimap.getValue(PREFIX_CONTACT_YEAR).get()));
        }

        // Parse Professor fields
        if (argMultimap.getValue(PREFIX_CONTACT_FACULTY).isPresent()) {
            editPersonDescriptor.setFaculty(ParserUtil.parseFaculty(
                    argMultimap.getValue(PREFIX_CONTACT_FACULTY).get()));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_DEPARTMENT).isPresent()) {
            editPersonDescriptor.setDepartment(ParserUtil.parseDepartment(
                    argMultimap.getValue(PREFIX_CONTACT_DEPARTMENT).get()));
        }

        if (argMultimap.getValue(PREFIX_CONTACT_OFFICE_HOURS).isPresent()) {
            editPersonDescriptor.setHours(ParserUtil.parseHours(
                    argMultimap.getValue(PREFIX_CONTACT_OFFICE_HOURS).get()));
        }

        Optional<Path> photoSource = Optional.empty();
        if (argMultimap.getValue(PREFIX_CONTACT_PHOTO).isPresent()) {
            String photoValue = argMultimap.getValue(PREFIX_CONTACT_PHOTO).get().trim();
            if (photoValue.equalsIgnoreCase(ParserUtil.BLANK_VALUE)) {
                editPersonDescriptor.setPhoto(null);
            } else {
                photoSource = Optional.of(ParserUtil.parsePhotoPath(photoValue));
            }
        }

        // Parse Tags (supports /tag or t/)
        List<String> rawTags = new ArrayList<>(argMultimap.getAllValues(PREFIX_TAG));
        rawTags.addAll(argMultimap.getAllValues(PREFIX_CONTACT_TAG));
        parseTagsForEdit(rawTags).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited() && photoSource.isEmpty()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor, photoSource);
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
