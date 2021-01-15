package nbbrd.console.picocli;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@lombok.Builder
public class ConfigHelper {

    public enum Scope {SYSTEM, GLOBAL, LOCAL}

    @NonNull
    public static ConfigHelper of(@NonNull String appName) {
        return builder().appName(appName).build();
    }

    @lombok.NonNull
    private final String appName;

    @lombok.NonNull
    private final SystemProperties system;

    @lombok.NonNull
    private final BiConsumer<Path, IOException> onLoadingError;

    public static Builder builder() {
        return new Builder()
                .system(SystemProperties.ofDefault())
                .onLoadingError(ConfigHelper::doNotReportError);
    }

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

    public void loadFile(@NonNull Properties properties, @Nullable Path file) {
        Objects.requireNonNull(properties);
        if (isValidFile(file)) {
            try (InputStream stream = Files.newInputStream(file)) {
                properties.load(stream);
            } catch (IOException ex) {
                onLoadingError.accept(file, ex);
            }
        }
    }

    private Path getConfigFile(Scope scope) {
        switch (scope) {
            case SYSTEM: {
                Predicate<Path> filterByAppName = path -> path.getFileName().toString().startsWith(appName);
                Path sibling = JarPathHelper.of(system).getJarPath(ConfigHelper.class, filterByAppName);
                return sibling != null ? sibling.getParent().resolve(getConfigFileName()) : null;
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

    private Path getConfigFileName() {
        return Paths.get(appName + ".properties");
    }

    static boolean isValidFile(@Nullable Path file) {
        return Objects.nonNull(file)
                && Files.exists(file)
                && Files.isReadable(file)
                && Files.isRegularFile(file);
    }

    static void doNotReportError(Path file, IOException error) {
    }
}
