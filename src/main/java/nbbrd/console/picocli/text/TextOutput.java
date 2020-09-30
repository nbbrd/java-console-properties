package nbbrd.console.picocli.text;

import nbbrd.console.properties.ConsoleProperties;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

public interface TextOutput {

    Path getFile();

    boolean isGzipped();

    boolean isAppend();

    Charset getEncoding();

    default Writer newCharWriter() throws IOException {
        if (hasFile()) {
            OutputStream stream = Files.newOutputStream(getFile(), CREATE, isAppend() ? APPEND : TRUNCATE_EXISTING);
            return new OutputStreamWriter(isGzipped() ? new GZIPOutputStream(stream) : stream, getEncoding());
        }
        return new OutputStreamWriter(new UncloseableOutputStream(getStdOutStream()), getStdOutEncoding());
    }

    default boolean hasFile() {
        return getFile() != null;
    }

    default boolean isAppending() throws IOException {
        return hasFile() && isAppend() && Files.exists(getFile()) && Files.size(getFile()) > 0;
    }

    default OutputStream getStdOutStream() {
        return System.out;
    }

    default Charset getStdOutEncoding() {
        return ConsoleProperties.ofServiceLoader().getStdOutEncoding().orElse(UTF_8);
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
