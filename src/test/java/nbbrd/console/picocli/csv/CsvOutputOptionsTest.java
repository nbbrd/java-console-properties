package nbbrd.console.picocli.csv;

import nbbrd.picocsv.Csv;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvOutputOptionsTest {

    @Test
    public void testNewCsvWriter() throws IOException {
        CsvOutputOptions options = new CsvOutputOptions();
        options.setEncoding(StandardCharsets.UTF_8);
        options.setSeparator(Csv.Format.RFC4180.getSeparator());
        options.setDelimiter(Csv.Format.RFC4180.getDelimiter());
        options.setQuote(Csv.Format.RFC4180.getQuote());


        try (StringWriter content = new StringWriter()) {
            try (Csv.Writer writer = options.newCsvWriter(content)) {
            }
            assertThat(content.toString()).isEmpty();
        }

        try (StringWriter content = new StringWriter()) {
            try (Csv.Writer writer = options.newCsvWriter(content)) {
                writer.writeField("A");
                writer.writeField("B");
                writer.writeEndOfLine();
                writer.writeField("C");
                writer.writeField("D");
            }
            assertThat(content.toString()).isEqualTo("A,B\r\nC,D");
        }
    }
}
