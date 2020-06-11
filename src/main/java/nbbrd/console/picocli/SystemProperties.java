package nbbrd.console.picocli;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@lombok.RequiredArgsConstructor(staticName = "of")
class SystemProperties {

    public static final String USER_HOME = "user.home";
    public static final String USER_DIR = "user.dir";
    public static final String PATH_SEPARATOR = "path.separator";
    public static final String JAVA_CLASS_PATH = "java.class.path";

    @NonNull
    public static SystemProperties ofDefault() {
        return of(System.getProperties());
    }

    @lombok.NonNull
    private final Properties source;

    @Nullable
    public Path getUserHome() {
        return getSystemPath(source.getProperty(USER_HOME));
    }

    @Nullable
    public Path getUserDir() {
        return getSystemPath(source.getProperty(USER_DIR));
    }

    public char getPathSeparator() {
        return getChar(source.getProperty(PATH_SEPARATOR));
    }

    @NonNull
    public List<Path> getClassPath() {
        return getSystemPaths(source.getProperty(JAVA_CLASS_PATH));
    }

    @Nullable
    private Path getSystemPath(String result) {
        return result != null ? Paths.get(result) : null;
    }

    private char getChar(String result) {
        return result != null && result.length() == 1 ? result.charAt(0) : '\0';
    }

    @NonNull
    private List<Path> getSystemPaths(String input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        return Stream.of(input.split("" + getPathSeparator(), -1))
                .map(Paths::get)
                .collect(Collectors.toList());
    }
}
