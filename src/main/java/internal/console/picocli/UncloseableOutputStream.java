package internal.console.picocli;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

@lombok.AllArgsConstructor
public final class UncloseableOutputStream extends OutputStream {

    @lombok.experimental.Delegate(excludes = Closeable.class)
    private final OutputStream delegate;

    @Override
    public void close() throws IOException {
        flush();
        super.close();
    }
}
