package nbbrd.console.picocli.text;

import nbbrd.console.picocli.FileSink;
import nbbrd.io.text.TextBuffers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

public interface TextOutput2 {

    boolean isAppend();

    Charset getEncoding();

    Path getStdoutFile();

    StdoutSink getStdoutSink();

    FileSink getFileSink();

    default BufferedWriter newBufferedWriter(Path file) throws IOException {
        if (isStdoutFile(file)) {
            return new BufferedWriter(getStdoutSink().newWriter());
        }
        CharsetEncoder encoder = getEncoding().newEncoder();
        TextBuffers buffers = TextBuffers.of(file, encoder);
        return new BufferedWriter(buffers.newCharWriter(newByteChannel(file), encoder), buffers.getCharBufferSize());
    }

    default void writeString(Path file, String text) throws IOException {
        try (Writer writer = newBufferedWriter(file)) {
            writer.write(text);
        }
    }

    default boolean isStdoutFile(Path file) {
        return file.equals(getStdoutFile());
    }

    default OpenOption[] getOpenOptions() {
        return new OpenOption[]{WRITE, CREATE, isAppend() ? APPEND : TRUNCATE_EXISTING};
    }

    default WritableByteChannel newByteChannel(Path file) throws IOException {
        return getFileSink().newByteChannel(file, getOpenOptions());
    }

    default boolean isAppending(Path file) throws IOException {
        return !isStdoutFile(file)
                && isAppend()
                && Files.exists(file)
                && Files.size(file) > 0;
    }

    String DEFAULT_ENCODING = "UTF-8";
}
