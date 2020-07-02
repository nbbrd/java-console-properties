package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextOutput;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Supplier;

public interface CsvOutput extends TextOutput {

    char getDelimiter();

    Csv.NewLine getSeparator();

    char getQuote();

    default Csv.Format toFormat() {
        return Csv.Format.RFC4180
                .toBuilder()
                .delimiter(getDelimiter())
                .quote(getQuote())
                .separator(getSeparator())
                .build();
    }

    default Csv.Writer newCsvWriter(Writer charWriter) throws IOException {
        return Csv.Writer.of(charWriter, toFormat());
    }

    default Csv.Writer newCsvWriter(Supplier<Optional<Charset>> stdOutEncoding) throws IOException {
        return newCsvWriter(newCharWriter(stdOutEncoding));
    }
}