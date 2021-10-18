package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextInput;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.io.Reader;

public interface CsvInput extends TextInput {

    char getDelimiter();

    CsvNewLine getSeparator();

    boolean isLenientSeparator();

    char getQuote();

    char getComment();

    default Csv.Format toFormat() {
        return Csv.Format.RFC4180
                .toBuilder()
                .delimiter(getDelimiter())
                .quote(getQuote())
                .separator(getSeparator().getSeparator())
                .comment(getComment())
                .build();
    }

    default Csv.ReaderOptions toParsing() {
        return Csv.ReaderOptions.builder().lenientSeparator(isLenientSeparator()).build();
    }

    default Csv.Reader newCsvReader(Reader charReader) throws IOException {
        return Csv.Reader.of(toFormat(), toParsing(), charReader, Csv.DEFAULT_CHAR_BUFFER_SIZE);
    }

    default Csv.Reader newCsvReader() throws IOException {
        return newCsvReader(newCharReader());
    }
}
