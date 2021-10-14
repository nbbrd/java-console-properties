package nbbrd.console.picocli.csv;

import nbbrd.picocsv.Csv;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvInputOptionsTest {

    @Test
    public void testNewCsvReader() throws IOException {
        CsvInputOptions options = new CsvInputOptions();
        options.setEncoding(StandardCharsets.UTF_8);
        options.setSeparator(CsvNewLine.parse(Csv.Format.RFC4180.getSeparator()));
        options.setDelimiter(Csv.Format.RFC4180.getDelimiter());
        options.setQuote(Csv.Format.RFC4180.getQuote());

        try (Csv.Reader reader = options.newCsvReader(new StringReader(""))) {
            assertThat(reader.readLine()).isFalse();
        }

        try (Csv.Reader reader = options.newCsvReader(new StringReader("A,B\r\n\"C\",D"))) {
            assertThat(readAll(reader))
                    .contains(new String[]{"A", "B"}, Index.atIndex(0))
                    .contains(new String[]{"C", "D"}, Index.atIndex(1))
                    .hasDimensions(2, 2);
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
