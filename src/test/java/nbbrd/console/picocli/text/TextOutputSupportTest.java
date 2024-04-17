package nbbrd.console.picocli.text;

import _test.Values;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static _test.Values.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class TextOutputSupportTest {

    @Test
    public void testIsAppending(@TempDir Path temp) throws IOException {
        final Path stdoutFile = temp.resolve("-");
        final Path notStdinFile = temp.resolve(".-");
        final Path regularFile = temp.resolve("regularFile.txt");
        final Path missingFile = temp.resolve("missingFile.txt");
        final Path emptyFile = temp.resolve("emptyFile.txt");
        final Path nonExistentFile = temp.resolve("nonExistentParent").resolve("nonExistentFile.txt");

        for (boolean gzipped : BOOLEANS) {
            for (Charset encoding : CHARSETS) {
                Files.deleteIfExists(stdoutFile);
                Files.deleteIfExists(notStdinFile);
                Files.deleteIfExists(regularFile);
                Files.deleteIfExists(missingFile);
                Files.deleteIfExists(emptyFile);
                Files.deleteIfExists(nonExistentFile);
                Files.deleteIfExists(nonExistentFile.getParent());

                Values.write(notStdinFile, encoding, gzipped, "notStdinFile");
                Values.write(regularFile, encoding, gzipped, "regularFile");
                Files.createFile(emptyFile);

                ByteArrayOutputStream stdout = new ByteArrayOutputStream();

                TextOutputSupport x = new TextOutputSupport();
                x.setFileEncoding(CharsetSupplier.of(encoding));
                x.setStdoutFile(stdoutFile);
                x.setStdoutSink(() -> stdout);
                x.setFileSink(fileSinkOf(gzipped));

                x.setAppend(false);
                assertThat(x.isAppending(stdoutFile)).isFalse();
                assertThat(x.isAppending(notStdinFile)).isFalse();
                assertThat(x.isAppending(regularFile)).isFalse();
                assertThat(x.isAppending(missingFile)).isFalse();
                assertThat(x.isAppending(emptyFile)).isFalse();
                assertThat(x.isAppending(nonExistentFile)).isFalse();

                x.setAppend(true);
                assertThat(x.isAppending(stdoutFile)).isFalse();
                assertThat(x.isAppending(notStdinFile)).isTrue();
                assertThat(x.isAppending(regularFile)).isTrue();
                assertThat(x.isAppending(missingFile)).isFalse();
                assertThat(x.isAppending(emptyFile)).isFalse();
                assertThat(x.isAppending(nonExistentFile)).isFalse();

                assertThat(stdout.toString(UTF_8.name())).isEmpty();
            }
        }
    }

    @Test
    public void testNewBufferedWriter(@TempDir Path temp) throws IOException {
        final Path stdoutFile = temp.resolve("-");
        final Path notStdinFile = temp.resolve(".-");
        final Path regularFile = temp.resolve("regularFile.txt");
        final Path missingFile = temp.resolve("missingFile.txt");
        final Path emptyFile = temp.resolve("emptyFile.txt");
        final Path nonExistentFile = temp.resolve("nonExistentParent").resolve("nonExistentFile.txt");

        for (boolean append : BOOLEANS) {
            for (boolean gzipped : BOOLEANS) {
                for (Charset encoding : CHARSETS) {
                    Files.deleteIfExists(stdoutFile);
                    Files.deleteIfExists(notStdinFile);
                    Files.deleteIfExists(regularFile);
                    Files.deleteIfExists(missingFile);
                    Files.deleteIfExists(emptyFile);
                    Files.deleteIfExists(nonExistentFile);
                    Files.deleteIfExists(nonExistentFile.getParent());

                    Values.write(notStdinFile, encoding, gzipped, "notStdinFile");
                    Values.write(regularFile, encoding, gzipped, "regularFile");
                    Files.createFile(emptyFile);

                    ByteArrayOutputStream stdout = new ByteArrayOutputStream();

                    TextOutputSupport x = new TextOutputSupport();
                    x.setAppend(append);
                    x.setFileEncoding(CharsetSupplier.of(encoding));
                    x.setStdoutFile(stdoutFile);
                    x.setStdoutSink(() -> stdout);
                    x.setFileSink(fileSinkOf(gzipped));

                    x.writeString(stdoutFile, "hello");
                    x.writeString(stdoutFile, " world");
                    assertThat(stdout.toString(UTF_8.name()))
                            .isEqualTo("hello world");

                    x.writeString(notStdinFile, " world");
                    assertThat(read(notStdinFile, encoding, gzipped))
                            .isEqualTo(append ? ("notStdinFile world") : " world");

                    x.writeString(regularFile, " regularContent");
                    assertThat(read(regularFile, encoding, gzipped))
                            .isEqualTo(append ? ("regularFile regularContent") : " regularContent");

                    x.writeString(missingFile, " missingContent");
                    assertThat(read(missingFile, encoding, gzipped))
                            .isEqualTo(" missingContent");

                    x.writeString(emptyFile, " emptyContent");
                    assertThat(read(emptyFile, encoding, gzipped))
                            .isEqualTo(" emptyContent");

                    x.writeString(nonExistentFile, " nonExistentContent");
                    assertThat(read(nonExistentFile, encoding, gzipped))
                            .isEqualTo(" nonExistentContent");
                }
            }
        }
    }
}
