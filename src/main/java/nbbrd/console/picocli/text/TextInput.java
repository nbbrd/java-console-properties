package nbbrd.console.picocli.text;

import nbbrd.io.Resource;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

public interface TextInput {

    Path getFile();

    boolean isGzipped();

    Charset getEncoding();

    InputStream getStdInStream();

    Charset getStdInEncoding();

    default Reader newCharReader() throws IOException {
        if (hasFile()) {
            InputStream stream = Files.newInputStream(getFile());
            return new InputStreamReader(isGzippedFile() ? new GZIPInputStream(stream) : stream, getEncoding());
        }
        return new InputStreamReader(Resource.uncloseableInputStream(getStdInStream()), getStdInEncoding());
    }

    default String readString() throws IOException {
        try (Reader reader = newCharReader()) {
            return TextInputSupport.readString(reader);
        }
    }

    default boolean hasFile() {
        return getFile() != null;
    }

    default boolean isGzippedFile() {
        return hasFile() && (isGzipped() || getFile().toString().toLowerCase(Locale.ROOT).endsWith(".gz"));
    }

    @Deprecated
    @lombok.AllArgsConstructor
    final class UncloseableInputStream extends InputStream {

        @lombok.experimental.Delegate(excludes = Closeable.class)
        private final InputStream delegate;
    }
}
