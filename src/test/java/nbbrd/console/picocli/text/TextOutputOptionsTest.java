package nbbrd.console.picocli.text;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Optional;

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
    public void testNewCharWriter() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            ByteArrayOutputStream stdOut = new ByteArrayOutputStream();

            TextOutputOptions options = new TextOutputOptions() {
                @Override
                public OutputStream newStandardOutputStream() {
                    return new UncloseableOutputStream(stdOut);
                }
            };

            options.setFile(null);
            for (boolean append : new boolean[]{false, true}) {
                for (Charset encoding : new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8}) {
                    options.setAppend(append);
                    options.setEncoding(null);

                    try (Writer charWriter = options.newCharWriter(() -> Optional.of(encoding))) {
                        charWriter.write("hello");
                    }
                    assertThat(stdOut.toByteArray()).asString(encoding).isEqualTo("hello");

                    try (Writer charWriter = options.newCharWriter(() -> Optional.of(encoding))) {
                        charWriter.write(" world");
                    }
                    assertThat(stdOut.toByteArray()).asString(encoding).isEqualTo("hello world");

                    stdOut.reset();
                }
            }

            options.setFile(fs.getPath("/file.txt"));
            for (boolean append : new boolean[]{false, true}) {
                for (Charset encoding : new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8}) {
                    options.setAppend(append);
                    options.setEncoding(encoding);

                    try (Writer charWriter = options.newCharWriter(Optional::empty)) {
                        charWriter.write("hello");
                    }
                    assertThat(options.getFile()).exists().hasContent("hello");

                    try (Writer charWriter = options.newCharWriter(Optional::empty)) {
                        charWriter.write(" world");
                    }
                    assertThat(options.getFile()).exists().hasContent(append ? "hello world" : " world");

                    Files.delete(options.getFile());
                }
            }
        }
    }
}
