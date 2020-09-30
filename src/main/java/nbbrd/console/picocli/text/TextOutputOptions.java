package nbbrd.console.picocli.text;

import nbbrd.console.picocli.StandardCharsetCandidates;
import picocli.CommandLine;

import java.nio.charset.Charset;
import java.nio.file.Path;

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
}
