package nbbrd.console.picocli;

import nbbrd.io.Resource;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface StdoutSink {

    OutputStream newOutputStream() throws IOException;

    static StdoutSink getDefault() {
        return () -> Resource.uncloseableOutputStream(System.out);
    }
}
