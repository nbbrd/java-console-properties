package nbbrd.console.picocli.text;

import internal.console.picocli.UncloseableOutputStream;
import nbbrd.console.properties.ConsoleProperties;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static java.nio.charset.StandardCharsets.UTF_8;

@FunctionalInterface
public interface StdoutSink {

    Writer newWriter() throws IOException;

    static StdoutSink getDefault() {
        return () ->
                new OutputStreamWriter(
                        new UncloseableOutputStream(System.out),
                        ConsoleProperties
                                .ofServiceLoader()
                                .getStdOutEncoding()
                                .orElse(UTF_8)
                                .newEncoder()
                );
    }

    String DEFAULT_STDOUT_FILE = "-";
}
