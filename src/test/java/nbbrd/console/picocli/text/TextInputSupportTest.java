package nbbrd.console.picocli.text;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static _test.Values.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;

public class TextInputSupportTest {

    @Test
    public void testNewBufferedReader() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Path stdinFile = fs.getPath("-");
            Path notStdinFile = fs.getPath("./-");
            Path regularFile = fs.getPath("/regularFile.txt");
            Path missingFile = fs.getPath("/missingFile.txt");

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

                    assertThat(input.readString(fs.getPath("./-")))
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
}
