package nbbrd.console.picocli.csv;

import internal.console.picocli.text.TextBuffering;
import nbbrd.console.picocli.text.TextInput2;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.nio.file.Path;

public interface PicocsvInput extends TextInput2 {

    Csv.Format getFormat();

    Csv.ReaderOptions getReaderOptions();

    default Csv.Reader newCsvReader(Path file) throws IOException {
        return TextBuffering.of(this, file, (reader, bufferSize) -> Csv.Reader.of(getFormat(), getReaderOptions(), reader, bufferSize));
    }
}
