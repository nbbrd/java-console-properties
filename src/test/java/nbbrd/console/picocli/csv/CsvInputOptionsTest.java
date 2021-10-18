package nbbrd.console.picocli.csv;

import nbbrd.picocsv.Csv;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvInputOptionsTest {

    @Test
    public void testNewCsvReaderOfChars() throws IOException {
        CsvInputOptions x = new CsvInputOptions();
        initCsvInput(x, Csv.Format.RFC4180);

        try (Csv.Reader reader = x.newCsvReader(new StringReader(""))) {
            assertThat(reader.readLine()).isFalse();
        }

        try (Csv.Reader reader = x.newCsvReader(new StringReader("#XYZ\r\nA,B\r\n\"C\",D"))) {
            assertThat(readAll(reader))
                    .contains(new String[]{"XYZ"}, Index.atIndex(0))
                    .contains(new String[]{"A", "B"}, Index.atIndex(1))
                    .contains(new String[]{"C", "D"}, Index.atIndex(2));
        }
    }

    @Test
    public void testToFormat() {
        CsvInputOptions x = new CsvInputOptions();
        initCsvInput(x, Csv.Format.RFC4180);

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

    private static void initCsvInput(CsvInputOptions x, Csv.Format format) {
        x.setSeparator(CsvNewLine.parse(format.getSeparator()));
        x.setDelimiter(format.getDelimiter());
        x.setQuote(format.getQuote());
        x.setComment(format.getComment());
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
