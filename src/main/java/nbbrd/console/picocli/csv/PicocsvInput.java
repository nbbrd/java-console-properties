package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextInput2;
import nbbrd.io.text.TextBuffers;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;

public interface PicocsvInput extends TextInput2 {

    Csv.Format getFormat();

    Csv.ReaderOptions getReaderOptions();

    default Csv.Reader newCsvReader(Path file) throws IOException {
        if (isStdInFile(file)) {
            return newCsvReader(getStdinSource().newReader(), Csv.DEFAULT_CHAR_BUFFER_SIZE);
        }
        CharsetDecoder decoder = getEncoding().newDecoder();
        TextBuffers buffers = TextBuffers.of(file, decoder);
        return newCsvReader(buffers.newCharReader(newByteChannel(file), decoder), buffers.getCharBufferSize());
    }

    default Csv.Reader newCsvReader(Reader charReader, int charBufferSize) throws IOException {
        return Csv.Reader.of(getFormat(), getReaderOptions(), charReader, charBufferSize);
    }
}
