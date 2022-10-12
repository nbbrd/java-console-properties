package nbbrd.console.picocli;

import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

import static internal.console.picocli.GzipFiles.isGzippedFileName;

public interface GzipInput {

    boolean isGzipped();

    default FileSource asFileSource() {
        return (file, options) ->
                isGzipped() || isGzippedFileName(file)
                        ? Channels.newChannel(new GZIPInputStream(Files.newInputStream(file, options)))
                        : Files.newByteChannel(file, options);
    }
}
