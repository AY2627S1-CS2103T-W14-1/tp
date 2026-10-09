package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String EMAIL_ARGUMENT = " /email " + VALID_EMAIL_BOB;
    private static final String PHONE_ARGUMENT = " /phone " + VALID_PHONE_BOB;
    private static final String LOCATION_ARGUMENT = " /location " + VALID_ADDRESS_BOB;
    private static final String VALID_ARGUMENTS = VALID_NAME_BOB + EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT;
    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allRequiredFields_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags().buildNoIndex();
        assertParseSuccess(parser, VALID_ARGUMENTS, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_fieldOrderVariations_success() {
        AddCommand expectedCommand = new AddCommand(new PersonBuilder(BOB).withTags().buildNoIndex());
        String[] fieldOrders = {
            EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT,
            EMAIL_ARGUMENT + LOCATION_ARGUMENT + PHONE_ARGUMENT,
            PHONE_ARGUMENT + EMAIL_ARGUMENT + LOCATION_ARGUMENT,
            PHONE_ARGUMENT + LOCATION_ARGUMENT + EMAIL_ARGUMENT,
            LOCATION_ARGUMENT + EMAIL_ARGUMENT + PHONE_ARGUMENT,
            LOCATION_ARGUMENT + PHONE_ARGUMENT + EMAIL_ARGUMENT
        };
        for (String fields : fieldOrders) {
            assertParseSuccess(parser, VALID_NAME_BOB + fields, expectedCommand);
        }
    }

    @Test
    public void parse_surroundingWhitespace_success() {
        assertParseSuccess(parser, "  " + VALID_NAME_BOB + "   /email   " + VALID_EMAIL_BOB
                + "   /phone   " + VALID_PHONE_BOB + "   /location   " + VALID_ADDRESS_BOB + "   ",
                new AddCommand(new PersonBuilder(BOB).withTags().buildNoIndex()));
    }

    @Test
    public void parse_optionalTags_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).buildNoIndex();
        assertParseSuccess(parser, VALID_ARGUMENTS + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, VALID_NAME_BOB + TAG_DESC_FRIEND + EMAIL_ARGUMENT + TAG_DESC_HUSBAND
                + PHONE_ARGUMENT + LOCATION_ARGUMENT, new AddCommand(expectedPerson));
        assertParseSuccess(parser, VALID_ARGUMENTS + TAG_DESC_FRIEND + TAG_DESC_FRIEND,
                new AddCommand(new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).buildNoIndex()));
    }

    @Test
    public void parse_requiredFieldMissing_failure() {
        assertParseFailure(parser, "", INVALID_FORMAT);
        assertParseFailure(parser, "   ", INVALID_FORMAT);
        assertParseFailure(parser, " " + EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT, INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_ARGUMENT + LOCATION_ARGUMENT, INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + LOCATION_ARGUMENT, INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + PHONE_ARGUMENT, INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_BOB, INVALID_FORMAT);
    }

    @Test
    public void parse_requiredValueBlank_failure() {
        assertParseFailure(parser, VALID_NAME_BOB + " /email   " + PHONE_ARGUMENT + LOCATION_ARGUMENT,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + " /phone   " + LOCATION_ARGUMENT,
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + PHONE_ARGUMENT + " /location",
                Address.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateRequiredPrefixes_failure() {
        Prefix[] prefixes = {PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_PHONE, PREFIX_CONTACT_LOCATION};
        String[] values = {VALID_EMAIL_BOB, VALID_PHONE_BOB, VALID_ADDRESS_BOB};
        for (int i = 0; i < prefixes.length; i++) {
            String duplicateError = Messages.getErrorMessageForDuplicatePrefixes(prefixes[i]);
            assertParseFailure(parser, VALID_ARGUMENTS + " " + prefixes[i] + " " + values[i], duplicateError);
            assertParseFailure(parser, VALID_ARGUMENTS + " " + prefixes[i] + " invalid*", duplicateError);
            assertParseFailure(parser, VALID_NAME_BOB + " " + prefixes[i] + " invalid*"
                    + EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT, duplicateError);
            assertParseFailure(parser, VALID_ARGUMENTS + " " + prefixes[i], duplicateError);
        }
        assertParseFailure(parser, VALID_ARGUMENTS + EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT,
                Messages.getErrorMessageForDuplicatePrefixes(prefixes));
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, "James&" + EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + " /email bob!yahoo" + PHONE_ARGUMENT + LOCATION_ARGUMENT,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + " /phone 911a" + LOCATION_ARGUMENT,
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ARGUMENTS + " t/hubby*", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_legacyAddSyntax_failure() {
        assertParseFailure(parser, " n/" + VALID_NAME_BOB + " e/" + VALID_EMAIL_BOB
                + " p/" + VALID_PHONE_BOB + " a/" + VALID_ADDRESS_BOB, INVALID_FORMAT);
        assertParseFailure(parser, " n/" + VALID_NAME_BOB + EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_incompletePrefixNames_failure() {
        assertParseFailure(parser, VALID_NAME_BOB + " /emails " + VALID_EMAIL_BOB
                + PHONE_ARGUMENT + LOCATION_ARGUMENT, INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + " /phones " + VALID_PHONE_BOB
                + LOCATION_ARGUMENT, INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + PHONE_ARGUMENT
                + " /locations " + VALID_ADDRESS_BOB, INVALID_FORMAT);
    }

    @Test
    public void parse_locationWithSlashText_success() {
        String location = "COM1 /location-map /phonebook 02-01";
        assertParseSuccess(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + PHONE_ARGUMENT + " /location " + location,
                new AddCommand(new PersonBuilder(BOB).withAddress(location).withTags().buildNoIndex()));
    }
}
