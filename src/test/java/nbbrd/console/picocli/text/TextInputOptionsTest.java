package nbbrd.console.picocli.text;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
        for (Charset encoding : new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8}) {
            options.setEncoding(null);
            stdInEncoding.set(encoding);

            try (BufferedReader reader = new BufferedReader(options.newCharReader())) {
                assertThat(reader.lines().collect(Collectors.joining())).isEqualTo("hello");
            }

            stdInStream.reset();
        }
    }

    @Test
    public void testNewCharReaderOfFile() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            TextInputOptions options = new TextInputOptions();

            options.setFile(fs.getPath("/file.txt"));
            for (Charset encoding : new Charset[]{StandardCharsets.US_ASCII, StandardCharsets.UTF_8}) {
                options.setEncoding(encoding);

                assertThatIOException().isThrownBy(options::newCharReader);

                Files.write(options.getFile(), Collections.singletonList("hello"));
                try (BufferedReader reader = new BufferedReader(options.newCharReader())) {
                    assertThat(reader.lines().collect(Collectors.joining())).isEqualTo("hello");
                }

                Files.delete(options.getFile());
            }
        }
    }
}
