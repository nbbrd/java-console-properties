package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextOutput2;
import nbbrd.io.text.TextBuffers;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;

public interface PicocsvOutput extends TextOutput2 {

    Csv.Format getFormat();

    Csv.WriterOptions getWriterOptions();

    default Csv.Writer newCsvWriter(Path file) throws IOException {
        if (isStdoutFile(file)) {
            return newCsvWriter(getStdoutSink().newWriter(), Csv.DEFAULT_CHAR_BUFFER_SIZE);
        }
        CharsetEncoder encoder = getEncoding().newEncoder();
        TextBuffers buffers = TextBuffers.of(file, encoder);
        return newCsvWriter(buffers.newCharWriter(newByteChannel(file), encoder), buffers.getCharBufferSize());
    }

    default Csv.Writer newCsvWriter(Writer charWriter, int charBufferSize) throws IOException {
        return Csv.Writer.of(getFormat(), getWriterOptions(), charWriter, charBufferSize);
    }
}
