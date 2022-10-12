package nbbrd.console.picocli.text;

import internal.console.picocli.text.Readers;
import nbbrd.console.picocli.FileSource;
import nbbrd.io.text.TextBuffers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.READ;

public interface TextInput2 {

    Charset getEncoding();

    Path getStdinFile();

    StdinSource getStdinSource();

    FileSource getFileSource();

    default BufferedReader newBufferedReader(Path file) throws IOException {
        if (isStdInFile(file)) {
            return new BufferedReader(getStdinSource().newReader());
        }
        CharsetDecoder decoder = getEncoding().newDecoder();
        TextBuffers buffers = TextBuffers.of(file, decoder);
        return new BufferedReader(buffers.newCharReader(newByteChannel(file), decoder), buffers.getCharBufferSize());
    }

    default String readString(Path file) throws IOException {
        try (Reader reader = newBufferedReader(file)) {
            return Readers.readString(reader);
        }
    }

    default boolean isStdInFile(Path file) {
        return file.equals(getStdinFile());
    }

    default OpenOption[] getOpenOptions() {
        return new OpenOption[]{READ};
    }

    default ReadableByteChannel newByteChannel(Path file) throws IOException {
        return getFileSource().newByteChannel(file, getOpenOptions());
    }

    String DEFAULT_ENCODING = "UTF-8";
}
