package nbbrd.console.picocli.text;

import internal.console.picocli.text.TextBuffering;
import nbbrd.console.picocli.FileSink;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public interface TextOutput2 {

    boolean isAppend();

    Charset getEncoding();

    Path getStdoutFile();

    StdoutSink getStdoutSink();

    FileSink getFileSink();

    default BufferedWriter newBufferedWriter(Path file) throws IOException {
        return TextBuffering.of(this, file, BufferedWriter::new);
    }

    default void writeString(Path file, String text) throws IOException {
        try (Writer writer = newBufferedWriter(file)) {
            writer.write(text);
        }
    }

    default boolean isStdoutFile(Path file) {
        return file.equals(getStdoutFile());
    }

    default boolean isAppending(Path file) throws IOException {
        return !isStdoutFile(file)
                && isAppend()
                && Files.exists(file)
                && Files.size(file) > 0;
    }

    String DEFAULT_ENCODING = "UTF-8";
}
