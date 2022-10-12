package nbbrd.console.picocli;

import nbbrd.io.function.IOUnaryOperator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

@FunctionalInterface
public interface FileSink {

    WritableByteChannel newByteChannel(Path file, OpenOption... options) throws IOException;

    static FileSink getDefault() {
        return Files::newByteChannel;
    }

    static FileSink of(IOUnaryOperator<OutputStream> mapper) {
        return (file, options) -> Channels.newChannel(mapper.applyWithIO(Files.newOutputStream(file, options)));
    }
}
