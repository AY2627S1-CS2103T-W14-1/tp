package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Holds exclusive access to an application's data file for the lifetime of a session.
 */
public final class DataFileLock implements AutoCloseable {
    // Closing any channel for the same file can release this process's OS lock on some platforms.
    // Reject repeat attempts in this JVM before opening another channel to that file.
    private static final Set<Path> OWNED_LOCK_FILES = new HashSet<>();

    private final FileChannel channel;
    private final FileLock lock;
    private final Path lockFile;

    private DataFileLock(FileChannel channel, FileLock lock, Path lockFile) {
        this.channel = channel;
        this.lock = lock;
        this.lockFile = lockFile;
    }

    /**
     * Attempts to acquire a non-blocking lock before the data file is read.
     * Returns empty if another session owns it; throws if the folder cannot be locked.
     * The sidecar is deliberately retained after closing: deleting it could allow another
     * process to lock a different file while an existing process still holds the old one.
     */
    public static synchronized Optional<DataFileLock> tryAcquire(Path dataFile) throws IOException {
        requireNonNull(dataFile);
        Path absoluteFile = dataFile.toAbsolutePath().normalize();
        Files.createDirectories(absoluteFile.getParent());
        Path lockFile = absoluteFile.getParent().toRealPath()
                .resolve(absoluteFile.getFileName() + ".lock");
        if (OWNED_LOCK_FILES.contains(lockFile)) {
            return Optional.empty();
        }
        FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        FileLock acquiredLock = null;
        try {
            try {
                acquiredLock = channel.tryLock();
            } catch (OverlappingFileLockException e) {
                return Optional.empty();
            }
            if (acquiredLock == null) {
                return Optional.empty();
            }
            OWNED_LOCK_FILES.add(lockFile);
            return Optional.of(new DataFileLock(channel, acquiredLock, lockFile));
        } finally {
            if (acquiredLock == null) {
                channel.close();
            }
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (DataFileLock.class) {
            // An old handle must not clear ownership after a newer session has acquired this path.
            if (!channel.isOpen()) {
                return;
            }
            try {
                if (lock.isValid()) {
                    lock.release();
                }
            } finally {
                try {
                    channel.close();
                } finally {
                    OWNED_LOCK_FILES.remove(lockFile);
                }
            }
        }
    }
}
