package _test;

import nbbrd.console.picocli.FileSink;
import nbbrd.console.picocli.FileSource;
import nbbrd.console.picocli.StdinSource;

import java.io.*;
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
        try (Writer writer = new OutputStreamWriter(fileSinkOf(compressed).newOutputStream(file, WRITE, CREATE), encoding)) {
            writer.append(content);
        }
    }

    public static String read(Path file, Charset encoding, boolean compressed) throws IOException {
        try (Reader reader = new InputStreamReader(fileSourceOf(compressed).newInputStream(file, READ), encoding)) {
            StringBuilder result = new StringBuilder();
            char[] buffer = new char[8 * 1024];
            int readCount = 0;
            while ((readCount = reader.read(buffer)) != -1) {
                result.append(buffer, 0, readCount);
            }
            return result.toString();
        }
    }

    public static StdinSource stdinSourceOf(String content) {
        return () -> new ByteArrayInputStream(content.getBytes(UTF_8));
    }

    public static FileSource fileSourceOf(boolean gzipped) {
        return gzipped ? FileSource.getDefault().andThen(GZIPInputStream::new) : FileSource.getDefault();
    }

    public static FileSink fileSinkOf(boolean gzipped) {
        return gzipped ? FileSink.getDefault().andThen(GZIPOutputStream::new) : FileSink.getDefault();
    }
}
