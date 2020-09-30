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
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class TextOutputOptionsTest {

    @Test
    public void testIsAppending() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            TextOutputOptions options = new TextOutputOptions();
            options.setFile(null);
            assertThat(options.isAppending()).isFalse();

            options.setFile(fs.getPath("/file.txt"));
            assertThat(options.isAppending()).isFalse();

            Files.createFile(options.getFile());
            assertThat(options.isAppending()).isFalse();

            Files.write(options.getFile(), Collections.singletonList("hello"));
            assertThat(options.isAppending()).isFalse();

            options.setAppend(true);
            assertThat(options.isAppending()).isTrue();
        }
    }

    @Test
    public void testNewCharWriterOfStdOut() throws IOException {
        ByteArrayOutputStream stdOutStream = new ByteArrayOutputStream();
        AtomicReference<Charset> stdOutEncoding = new AtomicReference<>(StandardCharsets.UTF_8);

        TextOutputOptions options = new TextOutputOptions() {
            @Override
            public OutputStream getStdOutStream() {
                return stdOutStream;
            }

            @Override
            public Charset getStdOutEncoding() {
                return stdOutEncoding.get();
            }
        };

        options.setFile(null);
        for (boolean append : getBooleans()) {
            for (boolean gzipped : getBooleans()) {
                for (Charset encoding : getCharsets()) {
                    options.setAppend(append);
                    options.setGzipped(gzipped);
                    options.setEncoding(encoding);

                    stdOutEncoding.set(encoding);

                    write(options, "hello");
                    assertThat(stdOutStream.toByteArray())
                            .describedAs("First append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                            .asString(encoding)
                            .isEqualTo("hello");

                    write(options, " world");
                    assertThat(stdOutStream.toByteArray())
                            .describedAs("Second append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                            .asString(encoding)
                            .isEqualTo("hello world");

                    stdOutStream.reset();
                }
            }
        }
    }

    @Test
    public void testNewCharWriterOfFile() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            TextOutputOptions options = new TextOutputOptions();

            options.setFile(fs.getPath("/file.txt"));
            for (boolean append : getBooleans()) {
                for (boolean gzipped : getBooleans()) {
                    for (Charset encoding : getCharsets()) {
                        options.setAppend(append);
                        options.setGzipped(gzipped);
                        options.setEncoding(encoding);

                        write(options, "hello");
                        assertThat(options.getFile())
                                .describedAs("First append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                                .exists()
                                .extracting(file -> read(file, encoding, gzipped))
                                .asString()
                                .isEqualTo("hello");

                        write(options, " world");
                        assertThat(options.getFile())
                                .describedAs("Second append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                                .exists()
                                .extracting(file -> read(file, encoding, gzipped))
                                .asString()
                                .isEqualTo(append ? "hello world" : " world");

                        Files.delete(options.getFile());
                    }
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

    private static void write(TextOutputOptions options, String content) throws IOException {
        try (Writer writer = options.newCharWriter()) {
            writer.write(content);
        }
    }

    private static String read(Path file, Charset encoding, boolean compressed) {
        try {
            if (!compressed) {
                return Files.readAllLines(file, encoding).stream().collect(Collectors.joining(System.lineSeparator()));
            } else {
                try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(file))) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(gzip, encoding))) {
                        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    }
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
