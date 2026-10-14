package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import seedu.address.model.person.Photo;

/**
 * Stores copied contact photos beside the address-book data file.
 */
public class PhotoStorage {

    static final String PHOTO_DIRECTORY_NAME = "photos";

    private final Path photoDirectory;

    /**
     * Creates photo storage in a {@value PHOTO_DIRECTORY_NAME} directory beside the address-book data file.
     */
    public PhotoStorage(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        photoDirectory = addressBookFilePath.toAbsolutePath().getParent().resolve(PHOTO_DIRECTORY_NAME);
    }

    /**
     * Copies the source image using a generated filename and returns its relative photo reference.
     */
    public Photo copyPhoto(Path sourcePath) throws IOException {
        requireNonNull(sourcePath);
        Files.createDirectories(photoDirectory);

        String extension = getExtension(sourcePath);
        String targetFileName = UUID.randomUUID() + extension;
        Path targetPath = photoDirectory.resolve(targetFileName);
        Files.copy(sourcePath, targetPath);

        return new Photo(PHOTO_DIRECTORY_NAME + "/" + targetFileName);
    }

    /**
     * Deletes the managed file represented by {@code photo}. References outside the photo directory are ignored.
     */
    public void deletePhoto(Photo photo) throws IOException {
        requireNonNull(photo);

        Path dataDirectory = photoDirectory.getParent();
        Path targetPath = dataDirectory.resolve(photo.getRelativePath()).normalize();
        if (targetPath.startsWith(photoDirectory)) {
            Files.deleteIfExists(targetPath);
        }
    }

    private static String getExtension(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            return "";
        }

        String name = fileName.toString();
        int extensionStart = name.lastIndexOf('.');
        return extensionStart < 0 ? "" : name.substring(extensionStart);
    }
}
