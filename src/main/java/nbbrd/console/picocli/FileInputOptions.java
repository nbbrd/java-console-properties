package nbbrd.console.picocli;

import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;

import static nbbrd.console.picocli.text.StdinSource.DEFAULT_STDIN_FILE;

@lombok.Getter
@lombok.Setter
public class FileInputOptions implements FileInput {

    @CommandLine.Option(
            names = {"-i", "--input"},
            paramLabel = "<file>",
            description = "Input file.",
            defaultValue = DEFAULT_STDIN_FILE
    )
    private Path file = Paths.get(DEFAULT_STDIN_FILE);
}
