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

public interface TextInput {

    Path getFile();

    Charset getEncoding();

    default Reader newCharReader(Supplier<Optional<Charset>> stdInEncoding) throws IOException {
        return getFile() != null
                ? new InputStreamReader(Files.newInputStream(getFile(), CREATE, TRUNCATE_EXISTING), getEncoding())
                : new InputStreamReader(new UncloseableInputStream(System.in), stdInEncoding.get().orElse(UTF_8));
    }

    @lombok.AllArgsConstructor
    static final class UncloseableInputStream extends InputStream {

        @lombok.experimental.Delegate(excludes = Closeable.class)
        private final InputStream delegate;
    }
}
