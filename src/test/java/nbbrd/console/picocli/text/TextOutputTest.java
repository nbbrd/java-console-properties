package nbbrd.console.picocli.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class TextOutputTest {

    @Test
    public void testIsAppending(@TempDir Path temp) throws IOException {
        TextOutputOptions options = new TextOutputOptions();
        options.setFile(null);
        assertThat(options.isAppending()).isFalse();

        options.setFile(temp.resolve("file.txt"));
        assertThat(options.isAppending()).isFalse();

        Files.createFile(options.getFile());
        assertThat(options.isAppending()).isFalse();

        Files.write(options.getFile(), Collections.singletonList("hello"));
        assertThat(options.isAppending()).isFalse();

        options.setAppend(true);
        assertThat(options.isAppending()).isTrue();
    }

    @Test
    public void testNewCharWriterOfStdOut() throws IOException {
        MockedTextOutput output = new MockedTextOutput();
        output.setStdOutStream(new ByteArrayOutputStream());
        output.setFile(null);

        for (boolean append : getBooleans()) {
            for (boolean gzipped : getBooleans()) {
                for (Charset encoding : getCharsets()) {
                    output.setAppend(append);
                    output.setGzipped(gzipped);
                    output.setEncoding(null);
                    output.setStdOutEncoding(encoding);

                    output.writeString("hello");
                    assertThat(output.getStdOutStream().toByteArray())
                            .describedAs("First append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                            .asString(encoding)
                            .isEqualTo("hello");

                    output.writeString(" world");
                    assertThat(output.getStdOutStream().toByteArray())
                            .describedAs("Second append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                            .asString(encoding)
                            .isEqualTo("hello world");

                    output.getStdOutStream().reset();
                }
            }
        }
    }

    @Test
    public void testNewCharWriterOfFile(@TempDir Path temp) throws IOException {
        MockedTextOutput output = new MockedTextOutput();
        output.setStdOutStream(null);
        output.setFile(temp.resolve("file.txt"));

        for (boolean append : getBooleans()) {
            for (boolean gzipped : getBooleans()) {
                for (Charset encoding : getCharsets()) {
                    output.setAppend(append);
                    output.setGzipped(gzipped);
                    output.setEncoding(encoding);
                    output.setStdOutEncoding(null);

                    output.writeString("hello");
                    assertThat(output.getFile())
                            .describedAs("First append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                            .exists()
                            .extracting(file -> read(file, encoding, gzipped))
                            .asString()
                            .isEqualTo("hello");

                    output.writeString(" world");
                    assertThat(output.getFile())
                            .describedAs("Second append:%s, gzipped:%s, encoding:%s", append, gzipped, encoding)
                            .exists()
                            .extracting(file -> read(file, encoding, gzipped))
                            .asString()
                            .isEqualTo(append ? "hello world" : " world");

                    Files.delete(output.getFile());
                }
            }
        }
    }

    @Test
    public void testIsGzippedFile(@TempDir Path temp) throws IOException {
        MockedTextOutput output = new MockedTextOutput();

        output.setGzipped(false);
        output.setFile(null);
        assertThat(output.isGzippedFile()).isFalse();

        output.setGzipped(true);
        output.setFile(null);
        assertThat(output.isGzippedFile()).isFalse();

        output.setGzipped(false);
        output.setFile(temp.resolve("file.txt"));
        assertThat(output.isGzippedFile()).isFalse();

        output.setGzipped(false);
        output.setFile(temp.resolve("file.txt.gz"));
        assertThat(output.isGzippedFile()).isTrue();

        output.setGzipped(true);
        output.setFile(temp.resolve("file.txt"));
        assertThat(output.isGzippedFile()).isTrue();

        output.setGzipped(true);
        output.setFile(temp.resolve("file.txt.gz"));
        assertThat(output.isGzippedFile()).isTrue();
    }

    @lombok.Data
    private static final class MockedTextOutput implements TextOutput {
        Path file;
        boolean gzipped;
        boolean append;
        Charset encoding;
        ByteArrayOutputStream stdOutStream;
        Charset stdOutEncoding;
    }

    private boolean[] getBooleans() {
        return new boolean[]{false, true};
    }

    private Charset[] getCharsets() {
        return new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8};
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
