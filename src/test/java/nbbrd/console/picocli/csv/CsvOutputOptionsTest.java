package nbbrd.console.picocli.csv;

import nbbrd.picocsv.Csv;
import nl.altindag.console.ConsoleCaptor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvOutputOptionsTest {

    @Test
    public void testNewCsvWriter() throws IOException {
        CsvOutputOptions x = new CsvOutputOptions();
        initCsvOutput(x, Csv.Format.RFC4180);

        try (ConsoleCaptor console = new ConsoleCaptor()) {
            try (Csv.Writer ignore = x.newCsvWriter()) {
            }
            assertThat(console.getStandardOutput()).isEmpty();
            assertThat(console.getErrorOutput()).isEmpty();

            try (Csv.Writer writer = x.newCsvWriter()) {
                writeSample(writer);
            }
            assertThat(console.getStandardOutput()).containsExactly("#XYZ", "A,B", "C,D");
            assertThat(console.getErrorOutput()).isEmpty();
        }
    }

    @Test
    public void testNewCsvWriterOfChars() throws IOException {
        CsvOutputOptions x = new CsvOutputOptions();
        initCsvOutput(x, Csv.Format.RFC4180);

        try (StringWriter chars = new StringWriter()) {
            try (Csv.Writer ignore = x.newCsvWriter(chars)) {
            }
            assertThat(chars.toString()).isEmpty();
        }

        try (StringWriter chars = new StringWriter()) {
            try (Csv.Writer writer = x.newCsvWriter(chars)) {
                writeSample(writer);
            }
            assertThat(chars.toString()).isEqualTo("#XYZ\r\nA,B\r\nC,D");
        }
    }

    @Test
    public void testToFormat() {
        CsvOutputOptions x = new CsvOutputOptions();
        initCsvOutput(x, Csv.Format.RFC4180);

        assertThat(x.toFormat())
                .isEqualTo(Csv.Format.RFC4180);

        x.setSeparator(CsvNewLine.MACINTOSH);
        x.setDelimiter('\t');
        x.setQuote('\'');
        x.setComment('$');
        assertThat(x.toFormat())
                .isEqualTo(Csv.Format
                        .builder()
                        .separator(Csv.Format.MACINTOSH_SEPARATOR)
                        .delimiter('\t')
                        .quote('\'')
                        .comment('$')
                        .build());
    }

    private static void initCsvOutput(CsvOutputOptions x, Csv.Format format) {
        x.setSeparator(CsvNewLine.parse(format.getSeparator()));
        x.setDelimiter(format.getDelimiter());
        x.setQuote(format.getQuote());
        x.setComment(format.getComment());
    }

    private static void writeSample(Csv.Writer writer) throws IOException {
        writer.writeComment("XYZ");
        writer.writeField("A");
        writer.writeField("B");
        writer.writeEndOfLine();
        writer.writeField("C");
        writer.writeField("D");
    }
}
