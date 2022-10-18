package nbbrd.console.picocli.text;

import lombok.AccessLevel;
import lombok.NonNull;
import nbbrd.console.picocli.ByteInputSupport;
import nbbrd.console.picocli.CommandSupporter;
import nbbrd.design.StaticFactoryMethod;
import nbbrd.io.text.TextBuffers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;

import static nbbrd.console.picocli.text.CharsetSupplier.DEFAULT_ENCODING;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TextInputSupport extends ByteInputSupport {

    @SafeVarargs
    @StaticFactoryMethod
    public static @NonNull TextInputSupport newTextInputSupport(@NonNull CommandSupporter<? super TextInputSupport>... supporters) {
        return CommandSupporter.create(TextInputSupport::new, supporters);
    }

    private @NonNull CharsetSupplier fileEncoding = CharsetSupplier.ofName(DEFAULT_ENCODING);

    private @NonNull CharsetSupplier stdinEncoding = CharsetSupplier.ofStdin();

    public @NonNull CharsetDecoder newDecoder(@NonNull Path file) {
        return isStdInFile(file) ? stdinEncoding.getCharset().newDecoder() : fileEncoding.getCharset().newDecoder();
    }

    public @NonNull BufferedReader newBufferedReader(@NonNull Path file) throws IOException {
        CharsetDecoder decoder = newDecoder(file);
        return new BufferedReader(newInputStreamReader(file, decoder), getCharBufferSize(file, decoder));
    }

    public @NonNull String readString(@NonNull Path file) throws IOException {
        try (Reader reader = newBufferedReader(file)) {
            return readString(reader);
        }
    }

    protected InputStreamReader newInputStreamReader(Path file, CharsetDecoder decoder) throws IOException {
        return new InputStreamReader(newInputStream(file), decoder);
    }

    protected int getCharBufferSize(Path file, CharsetDecoder decoder) throws IOException {
        return (isStdInFile(file) ? TextBuffers.UNKNOWN : TextBuffers.of(file, decoder)).getCharBufferSize();
    }

    static String readString(Reader reader) throws IOException {
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[8 * 1024];
        int readCount = 0;
        while ((readCount = reader.read(buffer)) != -1) {
            result.append(buffer, 0, readCount);
        }
        return result.toString();
    }
}
