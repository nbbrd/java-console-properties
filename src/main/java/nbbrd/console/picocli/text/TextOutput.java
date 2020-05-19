package nbbrd.console.picocli.text;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public interface TextOutput {

    Path getFile();

    Charset getEncoding();

    default Writer newCharWriter(Supplier<Optional<Charset>> stdOutEncoding) throws IOException {
        return getFile() != null
                ? new OutputStreamWriter(Files.newOutputStream(getFile(), CREATE, TRUNCATE_EXISTING), getEncoding())
                : new OutputStreamWriter(new UncloseableOutputStream(System.out), stdOutEncoding.get().orElse(UTF_8));
    }

    @lombok.AllArgsConstructor
    static final class UncloseableOutputStream extends OutputStream {

        @lombok.experimental.Delegate(excludes = Closeable.class)
        private final OutputStream delegate;

        @Override
        public void close() throws IOException {
            flush();
            super.close();
        }
    }
}
