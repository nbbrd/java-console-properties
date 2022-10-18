package nbbrd.console.picocli;

import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;

import static nbbrd.console.picocli.ByteInputSupport.DEFAULT_STDIN_FILE;


@lombok.Getter
@lombok.Setter
public class FileInputParameters {

    @CommandLine.Parameters(
            paramLabel = "<source>",
            description = "Input file (default: standard input).",
            defaultValue = DEFAULT_STDIN_FILE
    )
    private Path file = Paths.get(DEFAULT_STDIN_FILE);
}
