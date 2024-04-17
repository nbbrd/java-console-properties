package nbbrd.console.picocli.text;

import lombok.AccessLevel;
import lombok.NonNull;
import nbbrd.console.picocli.ByteOutputSupport;
import nbbrd.console.picocli.CommandSupporter;
import nbbrd.design.StaticFactoryMethod;
import nbbrd.io.text.TextBuffers;
import nbbrd.io.text.TextResource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TextOutputSupport extends ByteOutputSupport {

    @SafeVarargs
    @StaticFactoryMethod
    public static @NonNull TextOutputSupport newTextOutputSupport(@NonNull CommandSupporter<? super TextOutputSupport>... supporters) {
        return CommandSupporter.create(TextOutputSupport::new, supporters);
    }

    private @NonNull CharsetSupplier fileEncoding = CharsetSupplier.getDefault();

    private @NonNull CharsetSupplier stdoutEncoding = CharsetSupplier.ofStdout();

    public @NonNull CharsetEncoder newEncoder(@NonNull Path file) {
        return (isStdoutFile(file) ? stdoutEncoding.getCharset() : fileEncoding.getCharset()).newEncoder();
    }

    public @NonNull BufferedWriter newBufferedWriter(@NonNull Path file) throws IOException {
        CharsetEncoder encoder = newEncoder(file);
        return new BufferedWriter(newOutputStreamWriter(file, encoder), getCharBufferSize(file, encoder));
    }

    public void writeString(@NonNull Path file, @NonNull String text) throws IOException {
        try (Writer writer = newBufferedWriter(file)) {
            writer.write(text);
        }
    }

    protected OutputStreamWriter newOutputStreamWriter(Path file, CharsetEncoder encoder) throws IOException {
        return new OutputStreamWriter(newOutputStream(file), encoder);
    }

    protected int getCharBufferSize(Path file, CharsetEncoder encoder) throws IOException {
        return (isStdoutFile(file) ? TextBuffers.UNKNOWN : TextBuffers.of(file, encoder)).getCharBufferSize();
    }
}
