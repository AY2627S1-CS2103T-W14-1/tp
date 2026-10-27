package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
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
import seedu.address.model.person.Year;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String BLANK_VALUE = "[blank]";
    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";
    public static final String MESSAGE_INVALID_PHOTO_PATH = "Photo must be a readable PNG, JPG, JPEG, or GIF file.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(StringUtil.toProperCase(trimmedName));
    }

    /**
     * Parses a {@code String role} into a {@code Role}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code role} is invalid.
     */
    public static Role parseRole(String role) throws ParseException {
        requireNonNull(role);
        String trimmedRole = role.trim();
        if (!Role.isValidRole(trimmedRole)) {
            throw new ParseException(Role.MESSAGE_CONSTRAINTS);
        }
        return new Role(trimmedRole);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String major} into a {@code Major}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code major} is invalid.
     */
    public static Major parseMajor(String major) throws ParseException {
        if (major == null) {
            return null;
        }
        String trimmedMajor = major.trim();
        if (trimmedMajor.equalsIgnoreCase(BLANK_VALUE)) {
            return new Major(null);
        }
        if (!Major.isValidMajor(trimmedMajor)) {
            throw new ParseException(Major.MESSAGE_CONSTRAINTS);
        }
        return new Major(trimmedMajor);
    }

    /**
     * Parses a {@code String year} into a {@code Year}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code year} is invalid.
     */
    public static Year parseYear(String year) throws ParseException {
        if (year == null) {
            return null;
        }
        String trimmedYear = year.trim();
        if (trimmedYear.equalsIgnoreCase(BLANK_VALUE)) {
            return new Year(null);
        }
        if (!Year.isValidYear(trimmedYear)) {
            throw new ParseException(Year.MESSAGE_CONSTRAINTS);
        }
        return new Year(trimmedYear);
    }

    /**
     * Parses a {@code String faculty} into a {@code Faculty}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code faculty} is invalid.
     */
    public static Faculty parseFaculty(String faculty) throws ParseException {
        if (faculty == null) {
            return null;
        }
        String trimmedFaculty = faculty.trim();
        if (trimmedFaculty.equalsIgnoreCase(BLANK_VALUE)) {
            return new Faculty(null);
        }
        if (!Faculty.isValidFaculty(trimmedFaculty)) {
            throw new ParseException(Faculty.MESSAGE_CONSTRAINTS);
        }
        return new Faculty(trimmedFaculty);
    }

    /**
     * Parses a {@code String department} into a {@code Department}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code department} is invalid.
     */
    public static Department parseDepartment(String department) throws ParseException {
        if (department == null) {
            return null;
        }
        String trimmedDepartment = department.trim();
        if (trimmedDepartment.equalsIgnoreCase(BLANK_VALUE)) {
            return new Department(null);
        }
        if (!Department.isValidDepartment(trimmedDepartment)) {
            throw new ParseException(Department.MESSAGE_CONSTRAINTS);
        }
        return new Department(trimmedDepartment);
    }

    /**
     * Parses a {@code String hours} into a {@code Hours}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code hours} is invalid.
     */
    public static Hours parseHours(String hours) throws ParseException {
        if (hours == null) {
            return null;
        }
        String trimmedHours = hours.trim();
        if (trimmedHours.equalsIgnoreCase(BLANK_VALUE)) {
            return new Hours(null);
        }
        if (!Hours.isValidHours(trimmedHours)) {
            throw new ParseException(Hours.MESSAGE_CONSTRAINTS);
        }
        return new Hours(trimmedHours);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a photo source path and verifies that it is a readable supported image file.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the specified path is invalid or does not refer to a supported image file.
     */
    public static Path parsePhotoPath(String photoPath) throws ParseException {
        requireNonNull(photoPath);

        final Path path;
        try {
            path = Path.of(photoPath.trim()).toAbsolutePath().normalize();
        } catch (InvalidPathException exception) {
            throw new ParseException(MESSAGE_INVALID_PHOTO_PATH);
        }

        if (!Files.isRegularFile(path) || !Files.isReadable(path)
                || !hasSupportedImageExtension(path) || !hasReadableImageContents(path)) {
            throw new ParseException(MESSAGE_INVALID_PHOTO_PATH);
        }
        return path;
    }

    private static boolean hasSupportedImageExtension(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            return false;
        }

        String name = fileName.toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    private static boolean hasReadableImageContents(Path path) {
        try (ImageInputStream imageStream = ImageIO.createImageInputStream(path.toFile())) {
            if (imageStream == null) {
                return false;
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);
            if (!readers.hasNext()) {
                return false;
            }

            ImageReader reader = readers.next();
            try {
                reader.setInput(imageStream);
                return hasExpectedImageFormat(path, reader.getFormatName()) && reader.read(0) != null;
            } finally {
                reader.dispose();
            }
        } catch (java.io.IOException | RuntimeException exception) {
            return false;
        }
    }

    private static boolean hasExpectedImageFormat(Path path, String formatName) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        String normalizedFormatName = formatName.toLowerCase(Locale.ROOT);
        return (fileName.endsWith(".png") && normalizedFormatName.equals("png"))
                || ((fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
                && normalizedFormatName.equals("jpeg"))
                || (fileName.endsWith(".gif") && normalizedFormatName.equals("gif"));
    }
}
