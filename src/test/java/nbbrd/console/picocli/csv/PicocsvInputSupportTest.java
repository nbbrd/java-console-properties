package nbbrd.console.picocli.csv;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import nbbrd.console.picocli.text.CharsetSupplier;
import nbbrd.picocsv.Csv;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static _test.Values.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIOException;

public class PicocsvInputSupportTest {

    @Test
    public void testNewCsvReader() throws IOException {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Path stdinFile = fs.getPath("-");
            Path notStdinFile = fs.getPath("./-");
            Path regularFile = fs.getPath("/regularFile.txt");
            Path missingFile = fs.getPath("/missingFile.txt");

            for (boolean gzipped : BOOLEANS) {
                for (Charset encoding : CHARSETS) {
                    write(notStdinFile, encoding, gzipped, "notStdinFile");
                    write(regularFile, encoding, gzipped, "regularFile");

                    PicocsvInputSupport input = new PicocsvInputSupport();
                    input.setFileEncoding(CharsetSupplier.of(encoding));
                    input.setStdinFile(stdinFile);
                    input.setStdinSource(stdinSourceOf("stdinFile"));
                    input.setFileSource(fileSourceOf(gzipped));

                    assertThat(read(input, stdinFile))
                            .isDeepEqualTo(new String[][]{{"stdinFile"}});

                    assertThat(read(input, notStdinFile))
                            .isDeepEqualTo(new String[][]{{"notStdinFile"}});

                    assertThat(read(input, regularFile))
                            .isDeepEqualTo(new String[][]{{"regularFile"}});

                    assertThatIOException()
                            .isThrownBy(() -> read(input, missingFile))
                            .isInstanceOf(NoSuchFileException.class);

                    Files.delete(notStdinFile);
                    Files.delete(regularFile);
                }
            }
        }
    }

    private String[][] read(PicocsvInputSupport input, Path file) throws IOException {
        try (Csv.Reader reader = input.newCsvReader(file)) {
            return readAll(reader);
        }
    }

    private String[][] readAll(Csv.Reader reader) throws IOException {
        List<String[]> data = new ArrayList<>();
        while (reader.readLine()) {
            List<String> line = new ArrayList<>();
            while (reader.readField()) {
                line.add(reader.toString());
            }
            data.add(line.toArray(new String[0]));
        }
        return data.toArray(new String[0][]);
    }
}
