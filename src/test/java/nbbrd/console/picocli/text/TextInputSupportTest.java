package nbbrd.console.picocli.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static _test.Values.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;

public class TextInputSupportTest {

    @Test
    public void testNewBufferedReader(@TempDir Path temp) throws IOException {
        Path stdinFile = temp.resolve("-");
        Path notStdinFile = temp.resolve(".-");
        Path regularFile = temp.resolve("regularFile.txt");
        Path missingFile = temp.resolve("missingFile.txt");

        for (boolean gzipped : BOOLEANS) {
            for (Charset encoding : CHARSETS) {
                write(notStdinFile, encoding, gzipped, "notStdinFile");
                write(regularFile, encoding, gzipped, "regularFile");

                TextInputSupport input = new TextInputSupport();
                input.setFileEncoding(CharsetSupplier.of(encoding));
                input.setStdinFile(stdinFile);
                input.setStdinSource(stdinSourceOf("stdinFile"));
                input.setFileSource(fileSourceOf(gzipped));

                assertThat(input.readString(stdinFile))
                        .isEqualTo("stdinFile");

                assertThat(input.readString(temp.resolve(".-")))
                        .isEqualTo("notStdinFile");

                assertThat(input.readString(regularFile))
                        .isEqualTo("regularFile");

                assertThatIOException()
                        .isThrownBy(() -> input.readString(missingFile))
                        .isInstanceOf(NoSuchFileException.class);

                Files.delete(notStdinFile);
                Files.delete(regularFile);
            }
        }
    }
}
