package nbbrd.console.picocli;

import nbbrd.console.picocli.text.TextOutput;
import picocli.CommandLine;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

@CommandLine.Command(
        name = "generate-launcher",
        mixinStandardHelpOptions = true,
        description = {"Generate launcher script for ${ROOT-COMMAND-NAME:-the root command of this command}."},
        optionListHeading = "Options:%n",
        helpCommand = true
)
public class GenerateLauncher implements Callable<Void>, TextOutput {

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

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
        try (Writer w = newCharWriter()) {
            getType().append(w, getExecutableJar());
        }
        return null;
    }

    private Path getExecutableJar() {
        String appName = spec.root().name();
        Predicate<Path> filterByAppName = path -> path.getFileName().toString().startsWith(appName);
        return JarPathHelper.of(SystemProperties.ofDefault()).getJarPath(GenerateLauncher.class, filterByAppName);
    }

    @Override
    public Charset getEncoding() {
        return getType().charset;
    }

    @Override
    public boolean isAppend() {
        return false;
    }

    @lombok.AllArgsConstructor
    public enum LauncherType {
        BASH(StandardCharsets.US_ASCII) {
            @Override
            void append(Writer w, Path executableJar) throws IOException {
                w.append("#!/bin/sh\njava -jar \"")
                        .append(optionalPathToString(executableJar))
                        .append("\" \"$@\"");
            }
        },
        CMD(StandardCharsets.US_ASCII) {
            @Override
            void append(Writer w, Path executableJar) throws IOException {
                w.append("@java -jar \"")
                        .append(optionalPathToString(executableJar))
                        .append("\" %*");
            }
        };

        private final Charset charset;

        abstract void append(Writer w, Path classPath) throws IOException;

        static String optionalPathToString(Path path) {
            return path != null ? path.toString() : "";
        }
    }
}
