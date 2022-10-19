package nbbrd.console.picocli.text;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static _test.Values.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;

public class TextInputTest {

    @Test
    public void testNewCharReaderOfStdIn() throws IOException {
        MockedTextInput input = new MockedTextInput();
        input.setStdInStream(new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8)));
        input.setFile(null);

        for (boolean gzipped : BOOLEANS) {
            for (Charset encoding : CHARSETS) {
                input.setGzipped(gzipped);
                input.setEncoding(null);
                input.setStdInEncoding(encoding);

                assertThat(input.readString())
                        .isEqualTo("hello");

                input.getStdInStream().reset();
            }
        }
    }

    @Test
    public void testNewCharReaderOfFile() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            MockedTextInput input = new MockedTextInput();
            input.setStdInStream(null);
            input.setFile(fs.getPath("/file.txt"));

            for (boolean gzipped : BOOLEANS) {
                for (Charset encoding : CHARSETS) {
                    input.setGzipped(gzipped);
                    input.setEncoding(encoding);
                    input.setStdInEncoding(null);

                    assertThatIOException()
                            .describedAs("Missing file should throw IOException")
                            .isThrownBy(input::newCharReader);

                    write(input.getFile(), encoding, gzipped, "hello");
                    assertThat(input.readString())
                            .describedAs("Valid file should return valid content")
                            .isEqualTo("hello");

                    Files.delete(input.getFile());
                }
            }
        }
    }

    @Test
    public void testIsGzippedFile() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            MockedTextInput input = new MockedTextInput();

            input.setGzipped(false);
            input.setFile(null);
            assertThat(input.isGzippedFile()).isFalse();

            input.setGzipped(true);
            input.setFile(null);
            assertThat(input.isGzippedFile()).isFalse();

            input.setGzipped(false);
            input.setFile(fs.getPath("/file.txt"));
            assertThat(input.isGzippedFile()).isFalse();

            input.setGzipped(false);
            input.setFile(fs.getPath("/file.txt.gz"));
            assertThat(input.isGzippedFile()).isTrue();

            input.setGzipped(true);
            input.setFile(fs.getPath("/file.txt"));
            assertThat(input.isGzippedFile()).isTrue();

            input.setGzipped(true);
            input.setFile(fs.getPath("/file.txt.gz"));
            assertThat(input.isGzippedFile()).isTrue();
        }
    }

    @lombok.Data
    private final static class MockedTextInput implements TextInput {
        Path file;
        boolean gzipped;
        Charset encoding;
        ByteArrayInputStream stdInStream;
        Charset stdInEncoding;
    }
}
