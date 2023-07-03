package nbbrd.console.picocli;

import nbbrd.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface StdinSource {

    InputStream newInputStream() throws IOException;

    static StdinSource getDefault() {
        return () -> Resource.uncloseableInputStream(System.in);
    }
}
