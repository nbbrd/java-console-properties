package nbbrd.console.picocli;

import picocli.CommandLine;

import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;

import static internal.console.picocli.GzipFiles.isGzippedFileName;

@lombok.Getter
@lombok.Setter
public class GzipOutputOptions implements CommandSupporter<ByteOutputSupport> {

    @CommandLine.Option(
            names = {"-Z"},
            description = "Compress the output file with gzip.",
            defaultValue = "false"
    )
    private boolean gzipped = false;

    public FileSink asFileSink() {
        return (file, options) ->
                isGzipped() || isGzippedFileName(file)
                        ? new GZIPOutputStream(Files.newOutputStream(file, options))
                        : Files.newOutputStream(file, options);
    }

    @Override
    public void applyTo(ByteOutputSupport support) {
        support.setFileSink(asFileSink());
    }
}
