package nbbrd.console.picocli.csv;

import lombok.AccessLevel;
import lombok.NonNull;
import nbbrd.console.picocli.CommandSupporter;
import nbbrd.console.picocli.text.TextInputSupport;
import nbbrd.design.StaticFactoryMethod;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PicocsvInputSupport extends TextInputSupport {

    @SafeVarargs
    @StaticFactoryMethod
    public static @NonNull PicocsvInputSupport newPicocsvInputSupport(@NonNull CommandSupporter<? super PicocsvInputSupport>... supporters) {
        return CommandSupporter.create(PicocsvInputSupport::new, supporters);
    }

    private @NonNull Csv.Format format = Csv.Format.DEFAULT;

    private @NonNull Csv.ReaderOptions options = Csv.ReaderOptions.DEFAULT;

    public @NonNull Csv.Reader newCsvReader(@NonNull Path file) throws IOException {
        CharsetDecoder decoder = newDecoder(file);
        return Csv.Reader.of(getFormat(), getOptions(), newInputStreamReader(file, decoder), getCharBufferSize(file, decoder));
    }
}
