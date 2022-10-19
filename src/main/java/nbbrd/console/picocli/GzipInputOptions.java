package nbbrd.console.picocli;

import picocli.CommandLine;

import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

import static internal.console.picocli.GzipFiles.isGzippedFileName;

@lombok.Getter
@lombok.Setter
public class GzipInputOptions implements CommandSupporter<ByteInputSupport> {

    @CommandLine.Option(
            names = {"-z"},
            description = "Uncompress the input file with gzip.",
            defaultValue = "false"
    )
    private boolean gzipped = false;

    public FileSource asFileSource() {
        return (file, options) ->
                isGzipped() || isGzippedFileName(file)
                        ? new GZIPInputStream(Files.newInputStream(file, options))
                        : Files.newInputStream(file, options);
    }

    @Override
    public void applyTo(ByteInputSupport support) {
        support.setFileSource(asFileSource());
    }
}
