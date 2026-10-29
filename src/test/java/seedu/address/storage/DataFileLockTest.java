package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DataFileLockTest {
    @TempDir
    public Path testFolder;

    @Test
    public void tryAcquire_missingFolder_createsOnlyLockFile() throws Exception {
        Path dataFile = testFolder.resolve("data/contacts.json");
        try (DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(Files.exists(dataFile.resolveSibling("contacts.json.lock")));
            assertFalse(Files.exists(dataFile));
        }
    }

    @Test
    public void tryAcquire_sameFile_rejectsSecondSessionWithoutChangingData() throws Exception {
        Path dataFile = testFolder.resolve("contacts.json");
        Files.writeString(dataFile, "existing contacts");
        try (DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            assertTrue(DataFileLock.tryAcquire(testFolder.resolve("./contacts.json")).isEmpty());
            assertEquals("existing contacts", Files.readString(dataFile));
        }
    }

    @Test
    public void close_releasesLockWithoutDeletingSidecar() throws Exception {
        Path dataFile = testFolder.resolve("contacts.json");
        DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow();
        lock.close();
        lock.close();
        assertTrue(Files.exists(testFolder.resolve("contacts.json.lock")));
        try (DataFileLock reopened = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            lock.close();
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            assertEquals(2, runLockProcess(dataFile));
        }
    }

    @Test
    public void tryAcquire_differentFolders_allowsIndependentSessions() throws Exception {
        Path first = testFolder.resolve("first/contacts.json");
        Path second = testFolder.resolve("second/contacts.json");
        try (DataFileLock firstLock = DataFileLock.tryAcquire(first).orElseThrow();
                DataFileLock secondLock = DataFileLock.tryAcquire(second).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(first).isEmpty());
            assertTrue(DataFileLock.tryAcquire(second).isEmpty());
        }
    }

    @Test
    public void tryAcquire_invalidParent_throwsWithoutChangingExistingFile() throws Exception {
        Path parent = testFolder.resolve("not-a-directory");
        Files.writeString(parent, "keep me");
        assertThrows(IOException.class, () -> DataFileLock.tryAcquire(parent.resolve("contacts.json")));
        assertEquals("keep me", Files.readString(parent));
    }

    @Test
    public void tryAcquire_otherProcess_rejectsWhileHeldAndAllowsAfterExit() throws Exception {
        Path dataFile = testFolder.resolve("contacts.json");
        try (DataFileLock lock = DataFileLock.tryAcquire(dataFile).orElseThrow()) {
            assertTrue(DataFileLock.tryAcquire(dataFile).isEmpty());
            assertEquals(2, runLockProcess(dataFile));
        }
        assertEquals(0, runLockProcess(dataFile));
        // The previous subprocess did not close its channel; process termination releases it.
        assertEquals(0, runLockProcess(dataFile));
    }

    private int runLockProcess(Path dataFile) throws Exception {
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classPath = Path.of(DataFileLock.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                + File.pathSeparator
                + Path.of(DataFileLockProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Process process = new ProcessBuilder(javaExecutable, "-cp", classPath,
                DataFileLockProcess.class.getName(), dataFile.toString()).redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Lock subprocess timed out");
            return process.exitValue();
        } finally {
            process.destroyForcibly();
        }
    }
}
