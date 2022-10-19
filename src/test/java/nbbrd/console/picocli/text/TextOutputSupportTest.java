package nbbrd.console.picocli.text;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static _test.Values.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TextOutputSupportTest {

    @Test
    public void testIsAppending() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Path stdoutFile = fs.getPath("-");
            Path notStdinFile = fs.getPath("./-");
            Path regularFile = fs.getPath("/regularFile.txt");
            Path missingFile = fs.getPath("/missingFile.txt");
            Path emptyFile = fs.getPath("/emptyFile.txt");

            Files.write(notStdinFile, Collections.singletonList("notStdinFile"));
            Files.write(regularFile, Collections.singletonList("regularFile"));
            Files.createFile(emptyFile);

            for (Charset encoding : CHARSETS) {
                ByteArrayOutputStream stdout = new ByteArrayOutputStream();

                TextOutputSupport output = new TextOutputSupport();
                output.setFileEncoding(CharsetSupplier.of(encoding));
                output.setStdoutFile(stdoutFile);
                output.setStdoutSink(() -> stdout);

                output.setAppend(false);
                assertThat(output.isAppending(stdoutFile)).isFalse();
                assertThat(output.isAppending(notStdinFile)).isFalse();
                assertThat(output.isAppending(regularFile)).isFalse();
                assertThat(output.isAppending(missingFile)).isFalse();
                assertThat(output.isAppending(emptyFile)).isFalse();

                output.setAppend(true);
                assertThat(output.isAppending(stdoutFile)).isFalse();
                assertThat(output.isAppending(notStdinFile)).isTrue();
                assertThat(output.isAppending(regularFile)).isTrue();
                assertThat(output.isAppending(missingFile)).isFalse();
                assertThat(output.isAppending(emptyFile)).isFalse();

                assertThat(stdout.toString()).isEmpty();
            }
        }
    }

    @Test
    public void testNewBufferedWriter() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Path stdoutFile = fs.getPath("-");
            Path notStdinFile = fs.getPath("./-");
            Path regularFile = fs.getPath("/regularFile.txt");
            Path missingFile = fs.getPath("/missingFile.txt");
            Path emptyFile = fs.getPath("/emptyFile.txt");

            for (boolean append : BOOLEANS) {
                for (boolean gzipped : BOOLEANS) {
                    for (Charset encoding : CHARSETS) {
                        ByteArrayOutputStream stdout = new ByteArrayOutputStream();

                        TextOutputSupport output = new TextOutputSupport();
                        output.setAppend(append);
                        output.setFileEncoding(CharsetSupplier.of(encoding));
                        output.setStdoutFile(stdoutFile);
                        output.setStdoutSink(() -> stdout);
                        output.setFileSink(fileSinkOf(gzipped));

                        output.writeString(stdoutFile, "hello");
                        assertThat(stdout.toString())
                                .isEqualTo("hello");

                        output.writeString(stdoutFile, " world");
                        assertThat(stdout.toString())
                                .isEqualTo("hello world");

                        output.writeString(regularFile, "hello");
                        assertThat(read(regularFile, encoding, gzipped))
                                .isEqualTo("hello");

                        output.writeString(regularFile, " world");
                        assertThat(read(regularFile, encoding, gzipped))
                                .isEqualTo(append ? "hello world" : " world");

                        Files.delete(regularFile);
                    }
                }
            }
        }
    }
}
