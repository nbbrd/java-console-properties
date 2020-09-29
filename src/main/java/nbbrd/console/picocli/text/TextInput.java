package nbbrd.console.picocli.text;

import nbbrd.console.properties.ConsoleProperties;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface TextInput {

    Path getFile();

    Charset getEncoding();

    default Reader newCharReader() throws IOException {
        return hasFile()
                ? new InputStreamReader(Files.newInputStream(getFile()), getEncoding())
                : new InputStreamReader(new UncloseableInputStream(getStdInStream()), getStdInEncoding());
    }

    default boolean hasFile() {
        return getFile() != null;
    }

    default InputStream getStdInStream() {
        return System.in;
    }

    default Charset getStdInEncoding() {
        return ConsoleProperties.ofServiceLoader().getStdInEncoding().orElse(UTF_8);
    }

    @lombok.AllArgsConstructor
    final class UncloseableInputStream extends InputStream {

        @lombok.experimental.Delegate(excludes = Closeable.class)
        private final InputStream delegate;
    }
}
