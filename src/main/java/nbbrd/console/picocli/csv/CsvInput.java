package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextInput;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Supplier;

public interface CsvInput extends TextInput {

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

    default Csv.Parsing toParsing() {
        return Csv.Parsing.STRICT;
    }

    default Csv.Reader newCsvReader(Reader charReader) throws IOException {
        return Csv.Reader.of(charReader, toFormat(), toParsing());
    }

    default Csv.Reader newCsvReader(Supplier<Optional<Charset>> stdInEncoding) throws IOException {
        return newCsvReader(newCharReader(stdInEncoding));
    }
}