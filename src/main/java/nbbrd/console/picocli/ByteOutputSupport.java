package nbbrd.console.picocli;

import lombok.AccessLevel;
import lombok.NonNull;
import nbbrd.design.StaticFactoryMethod;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ByteOutputSupport {

    public static final String DEFAULT_STDOUT_FILE = "-";

    @SafeVarargs
    @StaticFactoryMethod
    public static @NonNull ByteOutputSupport newByteOutputSupport(@NonNull CommandSupporter<? super ByteOutputSupport>... supporters) {
        return CommandSupporter.create(ByteOutputSupport::new, supporters);
    }

    private @NonNull Path stdoutFile = Paths.get(DEFAULT_STDOUT_FILE);

    private @NonNull StdoutSink stdoutSink = StdoutSink.getDefault();

    private @NonNull FileSink fileSink = FileSink.getDefault();

    private boolean append = false;

    public boolean isStdoutFile(@NonNull Path file) {
        return file.equals(getStdoutFile());
    }

    public boolean isAppending(@NonNull Path file) throws IOException {
        return !isStdoutFile(file)
                && isAppend()
                && Files.exists(file)
                && Files.size(file) > 0;
    }

    public @NonNull OutputStream newOutputStream(@NonNull Path file) throws IOException {
        return isStdoutFile(file)
                ? getStdoutSink().newOutputStream()
                : getFileSink().newOutputStream(createNonExistentParents(file), getOutputOptions());
    }

    private Path createNonExistentParents(Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        return file;
    }

    private OpenOption[] getOutputOptions() {
        return new OpenOption[]{WRITE, CREATE, isAppend() ? APPEND : TRUNCATE_EXISTING};
    }
}
