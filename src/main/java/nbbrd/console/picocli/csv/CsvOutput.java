package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextOutput;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.io.Writer;

public interface CsvOutput extends TextOutput {

    char getDelimiter();

    CsvNewLine getSeparator();

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

    default Csv.Writer newCsvWriter(Writer charWriter) throws IOException {
        return Csv.Writer.of(toFormat(), Csv.WriterOptions.DEFAULT, charWriter, Csv.DEFAULT_CHAR_BUFFER_SIZE);
    }

    default Csv.Writer newCsvWriter() throws IOException {
        return newCsvWriter(newCharWriter());
    }
}
