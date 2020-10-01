package nbbrd.console.picocli.text;

import nbbrd.console.picocli.StandardCharsetCandidates;
import nbbrd.console.properties.ConsoleProperties;
import picocli.CommandLine;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

@lombok.Data
public class TextOutputOptions implements TextOutput {

    @CommandLine.Option(
            names = {"-o", "--output"},
            paramLabel = "<file>",
            description = "Output to a file instead of stdout."
    )
    private Path file;

    @CommandLine.Option(
            names = {"-z", "--gzipped"},
            description = "Compress the output file with gzip.",
            defaultValue = "false"
    )
    private boolean gzipped;

    @CommandLine.Option(
            names = {"--append"},
            description = "Append to the end of the output file.",
            defaultValue = "false"
    )
    private boolean append;

    @CommandLine.Option(
            names = {"-e", "--encoding"},
            paramLabel = "<encoding>",
            description = "Charset used to encode text.",
            completionCandidates = StandardCharsetCandidates.class,
            defaultValue = "UTF-8"
    )
    private Charset encoding;

    @Override
    public OutputStream getStdOutStream() {
        return System.out;
    }

    @Override
    public Charset getStdOutEncoding() {
        return ConsoleProperties
                .ofServiceLoader()
                .getStdOutEncoding()
                .orElse(UTF_8);
    }
}
