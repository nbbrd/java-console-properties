package internal.console.picocli;

import java.io.Closeable;
import java.io.InputStream;

@lombok.AllArgsConstructor
public final class UncloseableInputStream extends InputStream {

    @lombok.experimental.Delegate(excludes = Closeable.class)
    private final InputStream delegate;
}
