package nbbrd.console.picocli;

import nbbrd.console.picocli.text.TextOutput;
import nbbrd.console.properties.ConsoleProperties;
import picocli.CommandLine;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "generate-launcher",
        mixinStandardHelpOptions = true,
        description = {"Generate launcher script for ${PARENT-COMMAND-NAME:-the parent command of this command}."},
        optionListHeading = "Options:%n",
        helpCommand = true
)
public class GenerateLauncher implements Callable<Void>, TextOutput {

    @lombok.Getter
    @lombok.Setter
    @CommandLine.Option(
            names = {"-o", "--output"},
            paramLabel = "<file>",
            description = "Output to a file instead of stdout."
    )
    private Path file;

    @lombok.Getter
    @lombok.Setter
    @CommandLine.Option(
            names = {"-t", "--type"},
            paramLabel = "<type>",
            description = "Launcher type (${COMPLETION-CANDIDATES}).",
            defaultValue = "BASH"
    )
    private LauncherType type;

    @Override
    public Void call() throws IOException {
        String executableJar = getExecutableJar();
        try (Writer w = newCharWriter(this::getStdOutEncoding)) {
            getType().append(w, executableJar);
        }
        return null;
    }

    private String getExecutableJar() {
        return System.getProperty("java.class.path");
    }

    @Override
    public Charset getEncoding() {
        return getType().charset;
    }

    private Optional<Charset> getStdOutEncoding() {
        return ConsoleProperties.ofServiceLoader().getStdOutEncoding();
    }

    @lombok.AllArgsConstructor
    public enum LauncherType {
        BASH(StandardCharsets.US_ASCII) {
            @Override
            void append(Writer w, String executableJar) throws IOException {
                w.append("#!/bin/sh\njava -jar \"").append(executableJar).append("\" \"$@\"");
            }
        },
        CMD(StandardCharsets.US_ASCII) {
            @Override
            void append(Writer w, String executableJar) throws IOException {
                w.append("@java -jar \"").append(executableJar).append("\" %*");
            }
        };

        private final Charset charset;

        abstract void append(Writer w, String classPath) throws IOException;
    }
}
