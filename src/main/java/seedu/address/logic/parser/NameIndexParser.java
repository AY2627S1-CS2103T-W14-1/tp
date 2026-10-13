package seedu.address.logic.parser;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;

/** Parses a contact identifier in the form {@code NAME[_NUMBER]}. */
final class NameIndexParser {

    private NameIndexParser() {}

    /**
     * Parses a contact name and its optional one-based display index.
     *
     * @param args command arguments containing a contact identifier
     * @return the normalized name and corresponding storage index
     * @throws ParseException if the identifier is invalid
     */
    static NameAndIndex parse(String args) throws ParseException {
        if (args == null || args.isBlank() || args.indexOf('_') != args.lastIndexOf('_')) {
            throw new ParseException("Contact identifier must contain a valid name and at most one index");
        }

        args = args.trim();
        String name = args;
        int oneBasedIndex = 1;
        int underscoreIndex = args.indexOf('_');
        if (underscoreIndex >= 0) {
            name = args.substring(0, underscoreIndex);
            String indexText = args.substring(underscoreIndex + 1);
            if (!name.equals(name.trim())) {
                throw new ParseException("The person index must follow the name directly");
            }
            if (!StringUtil.isNonZeroUnsignedInteger(indexText)) {
                throw new ParseException("Person index must be a positive integer");
            }
            try {
                oneBasedIndex = Integer.parseInt(indexText);
            } catch (NumberFormatException exception) {
                throw new ParseException("Person index is too large", exception);
            }
        }

        name = StringUtil.toProperCase(name);
        if (!Name.isValidName(name)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new NameAndIndex(name, PersonIndex.fromOneBased(oneBasedIndex));
    }

    /** A normalized contact name paired with its index within the matching name bucket. */
    record NameAndIndex(String name, PersonIndex personIndex) {}
}
