package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

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
    public void copyPhoto_sameSourceTwice_createsIndependentCopies() throws Exception {
        Path sourcePath = testFolder.resolve("source.png");
        Files.writeString(sourcePath, "image content");
        PhotoStorage photoStorage = new PhotoStorage(testFolder.resolve("campuscontacts.json"));

        Photo firstPhoto = photoStorage.copyPhoto(sourcePath);
        Photo secondPhoto = photoStorage.copyPhoto(sourcePath);
        Path firstCopy = testFolder.resolve(firstPhoto.getRelativePath());
        Path secondCopy = testFolder.resolve(secondPhoto.getRelativePath());

        assertNotEquals(firstPhoto, secondPhoto);
        assertTrue(Files.exists(firstCopy));
        assertTrue(Files.exists(secondCopy));
        assertEquals("image content", Files.readString(firstCopy));
        assertEquals("image content", Files.readString(secondCopy));

        photoStorage.deletePhoto(firstPhoto);

        assertTrue(Files.notExists(firstCopy));
        assertTrue(Files.exists(secondCopy));
        assertTrue(Files.exists(sourcePath));
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

    @Test
    public void deleteUnreferencedPhotos_removesOrphansAndKeepsReferencedFiles() throws Exception {
        PhotoStorage photoStorage = new PhotoStorage(testFolder.resolve("campuscontacts.json"));
        Path referencedPath = testFolder.resolve("photos/referenced.png");
        Path orphanPath = testFolder.resolve("photos/orphan.jpg");
        Path unrelatedPath = testFolder.resolve("photos/notes.txt");
        Files.createDirectories(referencedPath.getParent());
        Files.writeString(referencedPath, "referenced");
        Files.writeString(orphanPath, "orphan");
        Files.writeString(unrelatedPath, "keep");

        photoStorage.deleteUnreferencedPhotos(Set.of(new Photo("photos/referenced.png")));

        assertTrue(Files.exists(referencedPath));
        assertTrue(Files.notExists(orphanPath));
        assertTrue(Files.exists(unrelatedPath));
    }
}
