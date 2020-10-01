package nbbrd.console.picocli;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

@lombok.RequiredArgsConstructor(staticName = "of")
public class JarPathHelper {

    @lombok.NonNull
    private final SystemProperties system;

    @Nullable
    public Path getJarPath(@NonNull Class<?> anchor, @NonNull Predicate<? super Path> filter) {
        Objects.requireNonNull(anchor);
        Objects.requireNonNull(filter);
        List<Path> classPath = system.getClassPath();
        switch (classPath.size()) {
            case 0:
                return null;
            case 1:
                return classPath.get(0).toAbsolutePath();
            default:
                Path result = getFromProtectionDomain(anchor);
                if (isValidJarPath(result)) {
                    return result;
                }
                result = getFromSystemClassLoader();
                if (isValidJarPath(result)) {
                    return result;
                }
                return classPath
                        .stream()
                        .filter(filter)
                        .findFirst()
                        .orElse(null);
        }
    }

    static Path getFromProtectionDomain(Class<?> anchor) {
        try {
            return urlToAbsolutePath(anchor.getProtectionDomain().getCodeSource().getLocation());
        } catch (SecurityException ex) {
            return null;
        }
    }

    static Path getFromSystemClassLoader() {
        return urlToAbsolutePath(ClassLoader.getSystemClassLoader().getResource("."));
    }

    static Path urlToAbsolutePath(URL url) {
        try {
            return url != null ? Paths.get(url.toURI()).toAbsolutePath() : null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    static boolean isValidJarPath(Path path) {
        return path != null && path.toString().toLowerCase(Locale.ROOT).endsWith(".jar");
    }
}
