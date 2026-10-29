package seedu.address.storage;

import java.nio.file.Path;

/** Test subprocess that exits without explicit cleanup to exercise operating-system lock release. */
public class DataFileLockProcess {
    public static void main(String[] args) throws Exception {
        DataFileLock lock = DataFileLock.tryAcquire(Path.of(args[0])).orElse(null);
        System.exit(lock == null ? 2 : 0);
    }
}
