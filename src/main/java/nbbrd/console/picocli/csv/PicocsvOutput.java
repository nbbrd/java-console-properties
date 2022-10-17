package nbbrd.console.picocli.csv;

import internal.console.picocli.text.TextBuffering;
import nbbrd.console.picocli.text.TextOutput2;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.nio.file.Path;

public interface PicocsvOutput extends TextOutput2 {

    Csv.Format getFormat();

    Csv.WriterOptions getWriterOptions();

    default Csv.Writer newCsvWriter(Path file) throws IOException {
        return TextBuffering.of(this, file, (writer, bufferSize) -> Csv.Writer.of(getFormat(), getWriterOptions(), writer, bufferSize));
    }
}
