package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Represents the relative path of a contact photo stored in the application's data directory.
 */
public class Photo {

    public static final String MESSAGE_CONSTRAINTS = "Photo path must not be blank.";

    private final String relativePath;

    /**
     * Creates a photo reference using a non-blank relative path.
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

    public static boolean isValidPhotoPath(String photoPath) {
        return photoPath != null && !photoPath.isBlank();
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
