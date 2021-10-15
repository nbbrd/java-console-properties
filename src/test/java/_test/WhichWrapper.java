package _test;

import nbbrd.io.sys.EndOfProcessException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;

@lombok.experimental.UtilityClass
public class WhichWrapper {

    public static final String COMMAND = "which";

    public boolean isAvailable(@NonNull String command) throws IOException {
        Process process = new ProcessBuilder(COMMAND, "/Q", command).start();
        try {
            switch (process.waitFor()) {
                case SUCCESSFUL_EXIT_CODE:
                    return true;
                case UNSUCCESSFUL_EXIT_CODE:
                    return false;
                case ERRORS_EXIT_CODE:
                default:
                    throw EndOfProcessException.of(process);
            }
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
    }

    private static final int SUCCESSFUL_EXIT_CODE = 0;
    private static final int UNSUCCESSFUL_EXIT_CODE = 1;
    private static final int ERRORS_EXIT_CODE = 2;
}
