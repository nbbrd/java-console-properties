package nbbrd.console.picocli;

import lombok.AccessLevel;
import lombok.NonNull;
import nbbrd.design.StaticFactoryMethod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.READ;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ByteInputSupport {

    public static final String DEFAULT_STDIN_FILE = "-";

    @SafeVarargs
    @StaticFactoryMethod
    public static @NonNull ByteInputSupport newByteInputSupport(@NonNull CommandSupporter<? super ByteInputSupport>... supporters) {
        return CommandSupporter.create(ByteInputSupport::new, supporters);
    }

    private @NonNull Path stdinFile = Paths.get(DEFAULT_STDIN_FILE);

    private @NonNull StdinSource stdinSource = StdinSource.getDefault();

    private @NonNull FileSource fileSource = FileSource.getDefault();

    public boolean isStdInFile(@NonNull Path file) {
        return file.equals(getStdinFile());
    }

    public @NonNull InputStream newInputStream(@NonNull Path file) throws IOException {
        return isStdInFile(file)
                ? getStdinSource().newInputStream()
                : getFileSource().newInputStream(file, READ);
    }
}
