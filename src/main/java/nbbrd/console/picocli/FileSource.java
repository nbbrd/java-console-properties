package nbbrd.console.picocli;

import nbbrd.io.function.IOUnaryOperator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@FunctionalInterface
public interface FileSource {

    InputStream newInputStream(Path file, OpenOption... options) throws IOException;

    default FileSource andThen(IOUnaryOperator<InputStream> mapper) {
        return (file, options) -> mapper.applyWithIO(newInputStream(file, options));
    }

    static FileSource getDefault() {
        return Files::newInputStream;
    }
}
