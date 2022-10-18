package nbbrd.console.picocli;

import internal.console.picocli.UncloseableOutputStream;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface StdoutSink {

    OutputStream newOutputStream() throws IOException;

    static StdoutSink getDefault() {
        return () -> new UncloseableOutputStream(System.out);
    }
}
