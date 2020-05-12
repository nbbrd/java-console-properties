package internal.console.picocli;

import nbbrd.io.function.IOConsumer;
import nbbrd.io.function.IOFunction;
import nbbrd.io.function.IOPredicate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RecursiveFiles {

    public static <X, Y> Function<X, Y> applyOrReport(IOFunction<X, Y> delegate, BiConsumer<Exception, X> report) {
        return value -> {
            try {
                return delegate.applyWithIO(value);
            } catch (IOException | RuntimeException ex) {
                report.accept(ex, value);
                return null;
            }
        };
    }

    public static Consumer<Path> acceptOrReport(IOConsumer<Path> delegate, BiConsumer<Exception, Path> report) {
        return value -> {
            try {
                delegate.acceptWithIO(value);
            } catch (IOException | RuntimeException ex) {
                report.accept(ex, value);
            }
        };
    }

    public static Stream<Path> walk(Path path, boolean recursive, IOPredicate<? super Path> filter) throws IOException {
        if (Files.isDirectory(path)) {
            return recursive
                    ? Files.walk(path).filter(filter.asUnchecked())
                    : StreamSupport.stream(Files.newDirectoryStream(path, filter::testWithIO).spliterator(), false);
        }
        return Stream.of(path);
    }
}
