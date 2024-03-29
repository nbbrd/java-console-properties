package nbbrd.console.picocli.text;

import nbbrd.console.picocli.Profilable;
import nbbrd.console.picocli.StandardCharsetCandidates;
import nbbrd.console.properties.ConsoleProperties;
import picocli.CommandLine;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

@lombok.Getter
@lombok.Setter
public class TextInputOptions implements TextInput, Profilable {

    @CommandLine.Option(
            names = {"-i", "--input"},
            paramLabel = "<file>",
            description = "Input from a file instead of stdin."
    )
    private Path file;

    @CommandLine.Option(
            names = {"-z", "--gzipped"},
            description = "Uncompress the output file with gzip.",
            defaultValue = "false"
    )
    private boolean gzipped;

    @CommandLine.Option(
            names = {"-e", "--encoding"},
            paramLabel = "<encoding>",
            description = "Charset used to encode text.",
            completionCandidates = StandardCharsetCandidates.class,
            defaultValue = "UTF-8"
    )
    private Charset encoding;

    @Override
    public InputStream getStdInStream() {
        return System.in;
    }

    @Override
    public Charset getStdInEncoding() {
        return ConsoleProperties
                .ofServiceLoader()
                .getStdInEncoding()
                .orElse(UTF_8);
    }
}
