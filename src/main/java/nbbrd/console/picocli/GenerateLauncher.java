package nbbrd.console.picocli;

import nbbrd.console.picocli.text.TextOutput;
import nbbrd.console.properties.ConsoleProperties;
import nbbrd.io.sys.SystemProperties;
import picocli.CommandLine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    @lombok.Getter
    @lombok.Setter
    @CommandLine.Option(
            names = {"--java"},
            paramLabel = "<file>",
            description = "Java bin path.",
            defaultValue = "java"
    )
    private Path javaBin;

    @Override
    public Void call() throws IOException {
        try (Writer w = newCharWriter()) {
            getType().append(w, getJavaBin(), getExecutableJar());
        }
        return null;
    }

    private Path getExecutableJar() {
        String appName = spec.root().name();
        Predicate<Path> filterByAppName = path -> path.getFileName().toString().startsWith(appName);
        return JarPathHelper.of(SystemProperties.DEFAULT).getJarPath(GenerateLauncher.class, filterByAppName);
    }

    @Override
    public Charset getEncoding() {
        return getType().charset;
    }

    @Override
    public boolean isAppend() {
        return false;
    }

    @Override
    public boolean isGzipped() {
        return false;
    }

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
    
    @lombok.AllArgsConstructor
    public enum LauncherType {
        BASH(StandardCharsets.US_ASCII) {
            @Override
            void append(Writer w, String java, String jar) throws IOException {
                w.append("#!/bin/sh\n")
                        .append(java)
                        .append(" -jar \"")
                        .append(jar)
                        .append("\" \"$@\"");
            }
        },
        CMD(StandardCharsets.US_ASCII) {
            @Override
            void append(Writer w, String java, String jar) throws IOException {
                w.append("@")
                        .append(java)
                        .append(" -jar \"")
                        .append(jar)
                        .append("\" %*");
            }
        },
        PS1(StandardCharsets.UTF_8) {
            @Override
            void append(Writer w, String java, String jar) throws IOException {
                w.append("if($myinvocation.expectingInput) { $input | & ")
                        .append(java)
                        .append(" -jar \"")
                        .append(jar)
                        .append("\" @args } else { & ")
                        .append(java)
                        .append(" -jar \"")
                        .append(jar)
                        .append("\" @args }");
            }
        };

        private final Charset charset;

        abstract void append(Writer w, String java, String jar) throws IOException;

        public void append(Writer w, Path javaBin, Path classPath) throws IOException {
            append(w, optionalPathToString(javaBin), optionalPathToString(classPath));
        }

        static String optionalPathToString(Path path) {
            return path != null ? path.toString() : "";
        }
    }
}
