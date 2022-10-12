package nbbrd.console.picocli;

import picocli.CommandLine;

@lombok.Getter
@lombok.Setter
public class GzipOutputOptions implements GzipOutput {

    @CommandLine.Option(
            names = {"-Z"},
            description = "Compress the output file with gzip.",
            defaultValue = "false"
    )
    private boolean gzipped = false;
}
