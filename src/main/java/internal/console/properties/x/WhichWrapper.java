package internal.console.properties.x;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;

@lombok.experimental.UtilityClass
public class WhichWrapper {

    public static final String COMMAND = "which";

    public boolean isAvailable(@NonNull String command) throws IOException {
        Process process = new ProcessBuilder(COMMAND, command).start();
        try {
            return process.waitFor() == NO_FAILED_ARG_EXIT_CODE;
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
    }

    private static final int NO_FAILED_ARG_EXIT_CODE = 0;
}
