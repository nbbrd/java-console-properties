package nbbrd.console.picocli;

import nbbrd.io.function.IOUnaryOperator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@FunctionalInterface
public interface FileSource {

    ReadableByteChannel newByteChannel(Path file, OpenOption... options) throws IOException;

    static FileSource getDefault() {
        return Files::newByteChannel;
    }

    static FileSource of(IOUnaryOperator<InputStream> mapper) {
        return (file, options) -> Channels.newChannel(mapper.applyWithIO(Files.newInputStream(file, options)));
    }
}
