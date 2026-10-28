package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.AddCommand.INVALID_PROFESSOR_PARAMETERS;
import static seedu.address.logic.commands.AddCommand.INVALID_STUDENT_PARAMETERS;
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
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_PHOTO;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONTACT_ROLE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.TreeSet;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
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
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String ROLE_ARGUMENT = " /role professor ";
    private static final String EMAIL_ARGUMENT = " /email " + VALID_EMAIL_BOB;
    private static final String PHONE_ARGUMENT = " /phone " + VALID_PHONE_BOB;
    private static final String LOCATION_ARGUMENT = " /location " + VALID_ADDRESS_BOB;
    private static final String VALID_ARGUMENTS = VALID_NAME_BOB + ROLE_ARGUMENT + EMAIL_ARGUMENT + PHONE_ARGUMENT
            + LOCATION_ARGUMENT;
    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    @TempDir
    public Path testFolder;

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allRequiredFields_success() {
        UnindexedPerson expectedPerson = new PersonBuilder(BOB).withTags().buildNoIndex();
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
            assertParseSuccess(parser, VALID_NAME_BOB + " /role professor" + fields, expectedCommand);
        }
    }

    @Test
    public void parse_surroundingWhitespace_success() {
        assertParseSuccess(parser, "  " + VALID_NAME_BOB + " /role professor" + "   /email   "
                + VALID_EMAIL_BOB + "   /phone   " + VALID_PHONE_BOB + "   /location   " + VALID_ADDRESS_BOB + "   ",
                new AddCommand(new PersonBuilder(BOB).withTags().buildNoIndex()));
    }

    @Test
    public void parse_optionalTags_success() {
        UnindexedPerson expectedPerson = new PersonBuilder(BOB)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).buildNoIndex();
        assertParseSuccess(parser, VALID_ARGUMENTS + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                new AddCommand(expectedPerson));
        assertParseSuccess(parser, VALID_NAME_BOB + TAG_DESC_FRIEND + EMAIL_ARGUMENT + TAG_DESC_HUSBAND
                + PHONE_ARGUMENT + LOCATION_ARGUMENT + ROLE_ARGUMENT, new AddCommand(expectedPerson));
        assertParseSuccess(parser, VALID_ARGUMENTS + TAG_DESC_FRIEND + TAG_DESC_FRIEND,
                new AddCommand(new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).buildNoIndex()));
    }

    @Test
    public void parse_photoPath_success() throws Exception {
        Path imageFile = testFolder.resolve("bob.png");
        ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), "png", imageFile.toFile());
        AddCommand expectedCommand = new AddCommand(new PersonBuilder(BOB).withTags().buildNoIndex(),
                Optional.of(imageFile.toAbsolutePath().normalize()));

        assertParseSuccess(parser, VALID_ARGUMENTS + " " + PREFIX_CONTACT_PHOTO + " " + imageFile,
                expectedCommand);
    }

    @Test
    public void parse_invalidPhotoPath_failure() {
        assertParseFailure(parser, VALID_ARGUMENTS + " " + PREFIX_CONTACT_PHOTO + " missing.png",
                ParserUtil.MESSAGE_INVALID_PHOTO_PATH);
    }

    @Test
    public void parse_invalidPhotoContents_failure() throws Exception {
        Path textFileWithImageExtension = testFolder.resolve("not-an-image.png");
        Files.writeString(textFileWithImageExtension, "not an image");

        assertParseFailure(parser, VALID_ARGUMENTS + " " + PREFIX_CONTACT_PHOTO + " "
                + textFileWithImageExtension, ParserUtil.MESSAGE_INVALID_PHOTO_PATH);
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
        assertParseFailure(parser, VALID_NAME_BOB + ROLE_ARGUMENT + " /email   " + PHONE_ARGUMENT
                        + LOCATION_ARGUMENT,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + ROLE_ARGUMENT + " /phone   "
                        + LOCATION_ARGUMENT,
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + PHONE_ARGUMENT + ROLE_ARGUMENT
                        + " /location",
                Address.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateRequiredPrefixes_failure() {
        Prefix[] prefixes = {PREFIX_CONTACT_ROLE, PREFIX_CONTACT_EMAIL, PREFIX_CONTACT_PHONE, PREFIX_CONTACT_LOCATION};
        String[] values = {ROLE_ARGUMENT, VALID_EMAIL_BOB, VALID_PHONE_BOB, VALID_ADDRESS_BOB};
        for (int i = 0; i < prefixes.length; i++) {
            String duplicateError = Messages.getErrorMessageForDuplicatePrefixes(prefixes[i]);
            assertParseFailure(parser, VALID_ARGUMENTS + " " + prefixes[i] + " " + values[i], duplicateError);
            assertParseFailure(parser, VALID_ARGUMENTS + " " + prefixes[i] + " invalid*", duplicateError);
            assertParseFailure(parser, VALID_NAME_BOB + ROLE_ARGUMENT + " " + prefixes[i] + " invalid*"
                    + EMAIL_ARGUMENT + PHONE_ARGUMENT + LOCATION_ARGUMENT, duplicateError);
            assertParseFailure(parser, VALID_ARGUMENTS + " " + prefixes[i], duplicateError);
        }
        assertParseFailure(parser, VALID_ARGUMENTS + ROLE_ARGUMENT + EMAIL_ARGUMENT + PHONE_ARGUMENT
                        + LOCATION_ARGUMENT,
                Messages.getErrorMessageForDuplicatePrefixes(prefixes));
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, "James&" + ROLE_ARGUMENT + EMAIL_ARGUMENT + PHONE_ARGUMENT
                        + LOCATION_ARGUMENT,
                Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + ROLE_ARGUMENT + " /email bob!yahoo" + PHONE_ARGUMENT
                        + LOCATION_ARGUMENT,
                Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_BOB + ROLE_ARGUMENT + EMAIL_ARGUMENT + " /phone 911a"
                        + LOCATION_ARGUMENT,
                Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ARGUMENTS + " t/hubby*", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_legacyAddSyntax_failure() {
        assertParseFailure(parser, " n/" + VALID_NAME_BOB + " e/" + VALID_EMAIL_BOB
                + " p/" + VALID_PHONE_BOB + " a/" + VALID_ADDRESS_BOB, INVALID_FORMAT);
        assertParseFailure(parser, " n/" + VALID_NAME_BOB + ROLE_ARGUMENT + EMAIL_ARGUMENT + PHONE_ARGUMENT
                        + LOCATION_ARGUMENT,
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
        assertParseSuccess(parser, VALID_NAME_BOB + EMAIL_ARGUMENT + ROLE_ARGUMENT + PHONE_ARGUMENT
                        + " /location " + location,
                new AddCommand(new PersonBuilder(BOB).withAddress(location).withTags().buildNoIndex()));
    }

    @Test
    public void parse_studentWithValidParameters_success() {
        assertParseSuccess(parser, "John Tan /role student /email johntan@example.com /phone 98672103 "
                + "/location Bedok /major Computer Science /year 2",
                new AddCommand(new UnindexedPerson(new Name("John Tan"), new Role("student"), new Phone("98672103"),
                        new Email("johntan@example.com"), new Address("Bedok"), new Major("Computer Science"),
                        new Year("2"), null, null, null, new TreeSet<Tag>(), null)));
    }

    @Test
    public void parse_professorWithValidParameters_success() {
        assertParseSuccess(parser, VALID_ARGUMENTS + " /faculty Faculty of Arts and Social Sciences "
                + "/department Department of Economics /hours 8am-5pm",
                new AddCommand(new UnindexedPerson(new Name("Bob Choo"), new Role("professor"), new Phone("22222222"),
                        new Email("bob@example.com"), new Address("Block 123, Bobby Street 3"), null, null,
                        new Faculty("Faculty of Arts and Social Sciences"), new Department("Department of Economics"),
                        new Hours("8am-5pm"), new TreeSet<Tag>(), null)));
    }

    @Test
    public void parse_studentWithNotApplicableParameters_failure() {
        assertParseFailure(parser, "John Tan /role student /email johntan@example.com /phone 98672103 "
                + "/location Bedok /faculty Faculty of Science", INVALID_STUDENT_PARAMETERS);
        assertParseFailure(parser, "John Tan /role student /email johntan@example.com /phone 98672103 "
                + "/location Bedok /department Department of Mathematics", INVALID_STUDENT_PARAMETERS);
        assertParseFailure(parser, "John Tan /role student /email johntan@example.com /phone 98672103 "
                + "/location Bedok /hours 8am-5pm", INVALID_STUDENT_PARAMETERS);
    }

    @Test
    public void parse_professorWithNotApplicableParameters_failure() {
        assertParseFailure(parser, "John Tan /role professor /email johntan@example.com /phone 98672103 "
                + "/location Bedok /major Computer Science", INVALID_PROFESSOR_PARAMETERS);
        assertParseFailure(parser, "John Tan /role professor /email johntan@example.com /phone 98672103 "
                + "/location Bedok /year 2", INVALID_PROFESSOR_PARAMETERS);
    }

    @Test
    public void parse_studentWithInvalidParameters_failure() {
        assertParseFailure(parser, "John Tan /role student /email johntan@example.com /phone 98672103 "
                + "/location Bedok /year 0", Year.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_professorWithInvalidParameters_failure() {
        assertParseFailure(parser, VALID_ARGUMENTS + " /hours ", Hours.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_academicFieldsWithPunctuation_success() {
        assertParseSuccess(parser, "John Tan /role student /email johntan@example.com /phone 98672103 "
                        + "/location Bedok /major Computer-Science /year 2",
                new AddCommand(new UnindexedPerson(new Name("John Tan"), new Role("student"), new Phone("98672103"),
                        new Email("johntan@example.com"), new Address("Bedok"), new Major("Computer-Science"),
                        new Year("2"), null, null, null, new TreeSet<Tag>(), null)));
        assertParseSuccess(parser, VALID_ARGUMENTS + " /faculty School_Of_Computing "
                        + "/department Bu5ine55 Analytic5 /hours 8am-5pm",
                new AddCommand(new UnindexedPerson(new Name("Bob Choo"), new Role("professor"), new Phone("22222222"),
                        new Email("bob@example.com"), new Address("Block 123, Bobby Street 3"), null, null,
                        new Faculty("School_Of_Computing"), new Department("Bu5ine55 Analytic5"),
                        new Hours("8am-5pm"), new TreeSet<>(), null)));
    }
}
