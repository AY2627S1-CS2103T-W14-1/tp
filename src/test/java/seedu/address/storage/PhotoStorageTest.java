package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.person.Photo;

public class PhotoStorageTest {

    @TempDir
    public Path testFolder;

    @Test
    public void copyPhoto_existingSource_copiesFileAndReturnsRelativeReference() throws Exception {
        Path sourcePath = testFolder.resolve("source.png");
        Files.writeString(sourcePath, "image content");
        PhotoStorage photoStorage = new PhotoStorage(testFolder.resolve("campuscontacts.json"));

        Photo photo = photoStorage.copyPhoto(sourcePath);
        Path copiedPath = testFolder.resolve(photo.getRelativePath());

        assertTrue(Files.exists(sourcePath));
        assertTrue(Files.exists(copiedPath));
        assertEquals("image content", Files.readString(copiedPath));
        assertTrue(photo.getRelativePath().startsWith("photos/"));
    }

    @Test
    public void deletePhoto_managedPhoto_deletesCopiedFile() throws Exception {
        Path sourcePath = testFolder.resolve("source.png");
        Files.writeString(sourcePath, "image content");
        PhotoStorage photoStorage = new PhotoStorage(testFolder.resolve("campuscontacts.json"));
        Photo photo = photoStorage.copyPhoto(sourcePath);

        photoStorage.deletePhoto(photo);

        assertTrue(Files.notExists(testFolder.resolve(photo.getRelativePath())));
        assertTrue(Files.exists(sourcePath));
    }

    @Test
    public void deletePhoto_missingManagedPhoto_doesNotThrow() throws Exception {
        PhotoStorage photoStorage = new PhotoStorage(testFolder.resolve("campuscontacts.json"));

        photoStorage.deletePhoto(new Photo("photos/missing.png"));

        assertTrue(Files.notExists(testFolder.resolve("photos/missing.png")));
    }
}
