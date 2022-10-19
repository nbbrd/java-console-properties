package nbbrd.console.picocli;

import nbbrd.io.function.IOUnaryOperator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@FunctionalInterface
public interface FileSink {

    OutputStream newOutputStream(Path file, OpenOption... options) throws IOException;

    default FileSink andThen(IOUnaryOperator<OutputStream> mapper) {
        return (file, options) -> mapper.applyWithIO(newOutputStream(file, options));
    }

    static FileSink getDefault() {
        return Files::newOutputStream;
    }
}
