package nbbrd.console.picocli;

import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;

import static internal.console.picocli.GzipFiles.isGzippedFileName;

public interface GzipOutput {

    boolean isGzipped();

    default FileSink asFileSink() {
        return (file, options) ->
                isGzipped() || isGzippedFileName(file)
                        ? Channels.newChannel(new GZIPOutputStream(Files.newOutputStream(file, options)))
                        : Files.newByteChannel(file, options);
    }
}
