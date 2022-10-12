package internal.console.picocli;

import java.nio.file.Path;
import java.util.Locale;

@lombok.experimental.UtilityClass
public class GzipFiles {

    public static boolean isGzippedFileName(Path file) {
        return file.toString().toLowerCase(Locale.ROOT).endsWith(".gz");
    }
}
