package _test;

import internal.console.picocli.text.Readers;
import nbbrd.console.picocli.FileSink;
import nbbrd.console.picocli.FileSource;
import nbbrd.console.picocli.text.StdinSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

@lombok.experimental.UtilityClass
public class Values {

    public static final List<Boolean> BOOLEANS = unmodifiableList(asList(false, true));

    public static final List<Charset> CHARSETS = unmodifiableList(asList(US_ASCII, UTF_8));

    public static void write(Path file, Charset encoding, boolean compressed, String content) throws IOException {
        try (Writer writer = Channels.newWriter(fileSinkOf(compressed).newByteChannel(file, WRITE, CREATE), encoding.name())) {
            writer.append(content);
        }
    }

    public static String read(Path file, Charset encoding, boolean compressed) throws IOException {
        try (Reader reader = Channels.newReader(fileSourceOf(compressed).newByteChannel(file, READ), encoding.name())) {
            return Readers.readString(reader);
        }
    }

    public static StdinSource stdinSourceOf(String content) {
        return () -> new StringReader(content);
    }

    public static FileSource fileSourceOf(boolean gzipped) {
        return gzipped ? FileSource.of(GZIPInputStream::new) : FileSource.getDefault();
    }

    public static FileSink fileSinkOf(boolean gzipped) {
        return gzipped ? FileSink.of(GZIPOutputStream::new) : FileSink.getDefault();
    }
}
