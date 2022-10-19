package nbbrd.console.picocli;

import internal.console.picocli.UncloseableInputStream;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface StdinSource {

    InputStream newInputStream() throws IOException;

    static StdinSource getDefault() {
        return () -> new UncloseableInputStream(System.in);
    }
}
