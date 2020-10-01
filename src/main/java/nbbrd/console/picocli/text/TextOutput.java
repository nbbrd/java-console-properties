package nbbrd.console.picocli.text;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import static java.nio.file.StandardOpenOption.*;

public interface TextOutput {

    Path getFile();

    boolean isGzipped();

    boolean isAppend();

    Charset getEncoding();

    OutputStream getStdOutStream();

    Charset getStdOutEncoding();

    default Writer newCharWriter() throws IOException {
        if (hasFile()) {
            OutputStream stream = Files.newOutputStream(getFile(), CREATE, isAppend() ? APPEND : TRUNCATE_EXISTING);
            return new OutputStreamWriter(isGzippedFile() ? new GZIPOutputStream(stream) : stream, getEncoding());
        }
        return new OutputStreamWriter(new UncloseableOutputStream(getStdOutStream()), getStdOutEncoding());
    }

    default void writeString(String text) throws IOException {
        try (Writer writer = newCharWriter()) {
            writer.write(text);
        }
    }

    default boolean hasFile() {
        return getFile() != null;
    }

    default boolean isGzippedFile() {
        return hasFile() && (isGzipped() || getFile().toString().toLowerCase(Locale.ROOT).endsWith(".gz"));
    }

    default boolean isAppending() throws IOException {
        return hasFile() && isAppend() && Files.exists(getFile()) && Files.size(getFile()) > 0;
    }

    @lombok.AllArgsConstructor
    final class UncloseableOutputStream extends OutputStream {

        @lombok.experimental.Delegate(excludes = Closeable.class)
        private final OutputStream delegate;

        @Override
        public void close() throws IOException {
            flush();
            super.close();
        }
    }
}
