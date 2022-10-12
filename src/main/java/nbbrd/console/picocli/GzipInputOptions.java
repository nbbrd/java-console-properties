package nbbrd.console.picocli;

import picocli.CommandLine;

@lombok.Getter
@lombok.Setter
public class GzipInputOptions implements GzipInput {

    @CommandLine.Option(
            names = {"-z"},
            description = "Uncompress the input file with gzip.",
            defaultValue = "false"
    )
    private boolean gzipped = false;
}
