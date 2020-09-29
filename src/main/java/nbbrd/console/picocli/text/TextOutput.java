package nbbrd.console.picocli.text;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

public interface TextOutput {

    Path getFile();

    Charset getEncoding();

    boolean isAppend();

    default boolean hasFile() {
        return getFile() != null;
    }

    default Writer newCharWriter(Supplier<Optional<Charset>> stdOutEncoding) throws IOException {
        return hasFile()
                ? new OutputStreamWriter(Files.newOutputStream(getFile(), CREATE, isAppend() ? APPEND : TRUNCATE_EXISTING), getEncoding())
                : new OutputStreamWriter(newStandardOutputStream(), stdOutEncoding.get().orElse(UTF_8));
    }

    default boolean isAppending() throws IOException {
        return hasFile() && isAppend() && Files.exists(getFile()) && Files.size(getFile()) > 0;
    }

    default OutputStream newStandardOutputStream() {
        return new UncloseableOutputStream(System.out);
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
