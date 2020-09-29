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
import java.util.concurrent.atomic.AtomicReference;

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
        ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
        AtomicReference<Charset> stdOutEncoding = new AtomicReference<>(StandardCharsets.UTF_8);

        TextOutputOptions options = new TextOutputOptions() {
            @Override
            public OutputStream getStdOutStream() {
                return stdOut;
            }

            @Override
            public Charset getStdOutEncoding() {
                return stdOutEncoding.get();
            }
        };

        options.setFile(null);
        for (boolean append : new boolean[]{false, true}) {
            for (Charset encoding : new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8}) {
                options.setAppend(append);
                options.setEncoding(null);
                stdOutEncoding.set(encoding);

                try (Writer charWriter = options.newCharWriter()) {
                    charWriter.write("hello");
                }
                assertThat(stdOut.toByteArray()).asString(encoding).isEqualTo("hello");

                try (Writer charWriter = options.newCharWriter()) {
                    charWriter.write(" world");
                }
                assertThat(stdOut.toByteArray()).asString(encoding).isEqualTo("hello world");

                stdOut.reset();
            }
        }
    }

    @Test
    public void testNewCharWriterOfFile() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            TextOutputOptions options = new TextOutputOptions();

            options.setFile(fs.getPath("/file.txt"));
            for (boolean append : new boolean[]{false, true}) {
                for (Charset encoding : new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8}) {
                    options.setAppend(append);
                    options.setEncoding(encoding);

                    try (Writer writer = options.newCharWriter()) {
                        writer.write("hello");
                    }
                    assertThat(options.getFile()).exists().hasContent("hello");

                    try (Writer writer = options.newCharWriter()) {
                        writer.write(" world");
                    }
                    assertThat(options.getFile()).exists().hasContent(append ? "hello world" : " world");

                    Files.delete(options.getFile());
                }
            }
        }
    }
}
