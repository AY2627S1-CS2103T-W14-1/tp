package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the relative path of a contact photo stored in the application's data directory.
 */
public class Photo {

    public static final String MESSAGE_CONSTRAINTS =
            "Photo path must be a valid image file inside the photos directory.";

    private final String relativePath;

    /**
     * Creates a photo reference to an image stored in the application's photos directory.
     */
    public Photo(String relativePath) {
        requireNonNull(relativePath);
        if (!isValidPhotoPath(relativePath)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    /**
     * Returns whether {@code photoPath} is a supported image path directly inside the photos directory.
     */
    public static boolean isValidPhotoPath(String photoPath) {
        if (photoPath == null || photoPath.isBlank()) {
            return false;
        }

        try {
            Path path = Path.of(photoPath).normalize();
            return !path.isAbsolute()
                    && path.getNameCount() == 2
                    && path.getName(0).toString().equals("photos")
                    && hasSupportedImageExtension(path);
        } catch (InvalidPathException exception) {
            return false;
        }
    }

    private static boolean hasSupportedImageExtension(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            return false;
        }

        String name = fileName.toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Photo otherPhoto)) {
            return false;
        }
        return relativePath.equals(otherPhoto.relativePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relativePath);
    }

    @Override
    public String toString() {
        return relativePath;
    }
}
