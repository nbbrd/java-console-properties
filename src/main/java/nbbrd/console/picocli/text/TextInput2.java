package nbbrd.console.picocli.text;

import internal.console.picocli.text.Readers;
import internal.console.picocli.text.TextBuffering;
import nbbrd.console.picocli.FileSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

public interface TextInput2 {

    Charset getEncoding();

    Path getStdinFile();

    StdinSource getStdinSource();

    FileSource getFileSource();

    default BufferedReader newBufferedReader(Path file) throws IOException {
        return TextBuffering.of(this, file, BufferedReader::new);
    }

    default String readString(Path file) throws IOException {
        try (Reader reader = newBufferedReader(file)) {
            return Readers.readString(reader);
        }
    }

    default boolean isStdInFile(Path file) {
        return file.equals(getStdinFile());
    }

    String DEFAULT_ENCODING = "UTF-8";
}
