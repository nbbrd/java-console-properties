package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.CharsetSupplier;
import nbbrd.picocsv.Csv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static _test.Values.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PicocsvOutputSupportTest {

    @Test
    public void testNewCsvWriter(@TempDir Path temp) throws IOException {
        Path stdoutFile = temp.resolve("-");
        Path notStdinFile = temp.resolve(".-");
        Path regularFile = temp.resolve("regularFile.txt");
        Path missingFile = temp.resolve("missingFile.txt");
        Path emptyFile = temp.resolve("emptyFile.txt");

        for (boolean append : BOOLEANS) {
            for (boolean gzipped : BOOLEANS) {
                for (Charset encoding : CHARSETS) {
                    ByteArrayOutputStream stdout = new ByteArrayOutputStream();

                    PicocsvOutputSupport output = new PicocsvOutputSupport();
                    output.setAppend(append);
                    output.setFileEncoding(CharsetSupplier.of(encoding));
                    output.setStdoutFile(stdoutFile);
                    output.setStdoutSink(() -> stdout);
                    output.setFileSink(fileSinkOf(gzipped));

                    write(output, stdoutFile, "hello");
                    assertThat(stdout.toString())
                            .isEqualTo("hello");

                    write(output, stdoutFile, " world");
                    assertThat(stdout.toString())
                            .isEqualTo("hello world");

                    write(output, regularFile, "hello");
                    assertThat(read(regularFile, encoding, gzipped))
                            .isEqualTo("hello");

                    write(output, regularFile, " world");
                    assertThat(read(regularFile, encoding, gzipped))
                            .isEqualTo(append ? "hello world" : " world");

                    Files.delete(regularFile);
                }
            }
        }
    }

    private void write(PicocsvOutputSupport output, Path file, String content) throws IOException {
        try (Csv.Writer writer = output.newCsvWriter(file)) {
            writer.writeField(content);
        }
    }
}
