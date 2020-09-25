package nbbrd.console.picocli;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@lombok.RequiredArgsConstructor(staticName = "of")
public class ConfigHelper {

    public enum Scope {SYSTEM, GLOBAL, LOCAL}

    @NonNull
    public static ConfigHelper of(@NonNull String appName) {
        return of(appName, SystemProperties.ofDefault());
    }

    @lombok.NonNull
    private final String appName;

    @lombok.NonNull
    private final SystemProperties system;

    public void loadAll(@NonNull Properties config) {
        Objects.requireNonNull(config);
        for (Scope scope : Scope.values()) {
            loadFile(config, getConfigFile(scope));
        }
    }

    public void load(@NonNull Properties config, @NonNull Scope scope) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(scope);
        loadFile(config, getConfigFile(scope));
    }

    private Path getConfigFile(Scope scope) {
        switch (scope) {
            case SYSTEM: {
                Path sibling = selectMainJar(system.getClassPath());
                return sibling != null ? sibling.toAbsolutePath().getParent().resolve(getConfigFileName()) : null;
            }
            case GLOBAL: {
                Path parent = system.getUserHome();
                return parent != null ? parent.resolve(getConfigFileName()) : null;
            }
            case LOCAL: {
                Path parent = system.getUserDir();
                return parent != null ? parent.resolve(getConfigFileName()) : null;
            }
            default:
                throw new RuntimeException();
        }
    }

    private Path selectMainJar(List<Path> classPath) {
        switch (classPath.size()) {
            case 0:
                return null;
            case 1:
                return classPath.get(0);
            default:
                return classPath.stream()
                        .filter(path -> path.getFileName().toString().startsWith(appName))
                        .findFirst()
                        .orElse(null);
        }
    }

    private Path getConfigFileName() {
        return Paths.get(appName + ".properties");
    }

    private boolean isValidFile(@Nullable Path file) {
        return Objects.nonNull(file)
                && Files.exists(file)
                && Files.isReadable(file)
                && Files.isRegularFile(file);
    }

    public void loadFile(Properties properties, Path file) {
        if (isValidFile(file)) {
            try (InputStream stream = Files.newInputStream(file)) {
                properties.load(stream);
            } catch (IOException ex) {
//            log.log(Level.WARNING, "While loading file '" + file + "'", ex);
            }
        }
    }
}
