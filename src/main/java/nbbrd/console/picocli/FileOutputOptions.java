package nbbrd.console.picocli;

import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;

import static nbbrd.console.picocli.text.StdoutSink.DEFAULT_STDOUT_FILE;

@lombok.Getter
@lombok.Setter
public class FileOutputOptions implements FileOutput {

    @CommandLine.Option(
            names = {"-o", "--output"},
            paramLabel = "<file>",
            description = "Output file.",
            defaultValue = DEFAULT_STDOUT_FILE
    )
    private Path file = Paths.get(DEFAULT_STDOUT_FILE);
}
