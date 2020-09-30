package nbbrd.console.picocli.text;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;

public class TextInputOptionsTest {

    @Test
    public void testNewCharReaderOfStdIn() throws IOException {
        ByteArrayInputStream stdInStream = new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8));
        AtomicReference<Charset> stdInEncoding = new AtomicReference<>(StandardCharsets.UTF_8);

        TextInputOptions options = new TextInputOptions() {
            @Override
            public InputStream getStdInStream() {
                return stdInStream;
            }

            @Override
            public Charset getStdInEncoding() {
                return stdInEncoding.get();
            }
        };

        options.setFile(null);
        for (boolean gzipped : getBooleans()) {
            for (Charset encoding : getCharsets()) {
                options.setGzipped(gzipped);
                options.setEncoding(encoding);

                stdInEncoding.set(encoding);

                assertThat(read(options)).isEqualTo("hello");

                stdInStream.reset();
            }
        }
    }

    @Test
    public void testNewCharReaderOfFile() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            TextInputOptions options = new TextInputOptions();

            options.setFile(fs.getPath("/file.txt"));
            for (boolean gzipped : getBooleans()) {
                for (Charset encoding : getCharsets()) {
                    options.setGzipped(gzipped);
                    options.setEncoding(encoding);

                    assertThatIOException().isThrownBy(options::newCharReader);

                    write(options.getFile(), encoding, gzipped, "hello");
                    assertThat(read(options)).isEqualTo("hello");

                    Files.delete(options.getFile());
                }
            }
        }
    }

    private boolean[] getBooleans() {
        return new boolean[]{false, true};
    }

    private Charset[] getCharsets() {
        return new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8};
    }

    private static void write(Path file, Charset encoding, boolean compressed, String content) throws IOException {
        if (!compressed) {
            Files.write(file, Collections.singletonList(content));
        } else {
            try (GZIPOutputStream gzip = new GZIPOutputStream(Files.newOutputStream(file))) {
                try (Writer writer = new OutputStreamWriter(gzip, encoding)) {
                    writer.append(content);
                }
            }
        }
    }

    private static String read(TextInputOptions options) throws IOException {
        try (BufferedReader reader = new BufferedReader(options.newCharReader())) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
