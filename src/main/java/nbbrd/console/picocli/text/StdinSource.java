package nbbrd.console.picocli.text;

import internal.console.picocli.UncloseableInputStream;
import nbbrd.console.properties.ConsoleProperties;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;

@FunctionalInterface
public interface StdinSource {

    Reader newReader() throws IOException;

    static StdinSource getDefault() {
        return () ->
                new InputStreamReader(
                        new UncloseableInputStream(System.in),
                        ConsoleProperties
                                .ofServiceLoader()
                                .getStdInEncoding()
                                .orElse(UTF_8)
                );
    }

    String DEFAULT_STDIN_FILE = "-";
}
