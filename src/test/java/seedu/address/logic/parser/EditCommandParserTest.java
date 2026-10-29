package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_LOCATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.PersonIndex;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no target name specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "Alice Pauline", EditCommand.MESSAGE_NOT_EDITED);

        // no target name and no field specified
        assertParseFailure(parser, " /phone 91234567", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_blankPhoto_clearsPhoto() {
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setPhoto(null);
        EditCommand expectedCommand = new EditCommand("Alice Pauline", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());

        assertParseSuccess(parser, "Alice Pauline /photo [blank]", expectedCommand);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // malformed identifier suffix
        assertParseFailure(parser, "Alice Pauline_-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "Alice Pauline_0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "Alice Pauline 123", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "Alice Pauline i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "Alice Pauline" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "Alice Pauline" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "Alice Pauline" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "Alice Pauline" + INVALID_ADDRESS_DESC,
                Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "Alice Pauline" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "Alice Pauline" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "Alice Pauline" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "Alice Pauline" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "Alice Pauline" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "Alice Pauline" + INVALID_NAME_DESC + INVALID_EMAIL_DESC
                + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        String userInput = "Benson Meier" + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand("Benson Meier", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        String userInput = "Alice Pauline" + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand("Alice Pauline", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        String userInput = "Carl Kurz" + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand("Carl Kurz", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = "Carl Kurz" + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand("Carl Kurz", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = "Carl Kurz" + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand("Carl Kurz", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = "Carl Kurz" + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand("Carl Kurz", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = "Carl Kurz" + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand("Carl Kurz", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        String userInput = "Alice Pauline" + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CONTACT_PHONE));

        // invalid followed by valid
        userInput = "Alice Pauline" + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CONTACT_PHONE));

        // multiple valid fields repeated
        userInput = "Alice Pauline" + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CONTACT_PHONE, PREFIX_CONTACT_EMAIL,
                        PREFIX_CONTACT_LOCATION));

        // multiple invalid values
        userInput = "Alice Pauline" + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CONTACT_PHONE, PREFIX_CONTACT_EMAIL,
                        PREFIX_CONTACT_LOCATION));
    }

    @Test
    public void parse_resetTags_success() {
        String userInput = "Carl Kurz" + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand("Carl Kurz", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_campusContactsPrefixes_success() {
        String userInput = "Benson Meier /phone 91234567 /email test@example.com /location UTown";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone("91234567").withEmail("test@example.com").withAddress("UTown").build();
        EditCommand expectedCommand = new EditCommand("Benson Meier", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_profileFields_success() {
        String userInput = "Alice Pauline /major Computer Science /year 2";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withMajor("Computer Science").withYear("2").build();
        EditCommand expectedCommand = new EditCommand("Alice Pauline", PersonIndex.fromOneBased(1), descriptor,
                java.util.Optional.empty());
        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
