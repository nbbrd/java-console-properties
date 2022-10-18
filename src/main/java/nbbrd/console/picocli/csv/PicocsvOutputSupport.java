package nbbrd.console.picocli.csv;

import lombok.AccessLevel;
import lombok.NonNull;
import nbbrd.console.picocli.CommandSupporter;
import nbbrd.console.picocli.text.TextOutputSupport;
import nbbrd.design.StaticFactoryMethod;
import nbbrd.picocsv.Csv;

import java.io.IOException;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PicocsvOutputSupport extends TextOutputSupport {

    @SafeVarargs
    @StaticFactoryMethod
    public static @NonNull PicocsvOutputSupport newPicocsvOutputSupport(@NonNull CommandSupporter<? super PicocsvOutputSupport>... supporters) {
        return CommandSupporter.create(PicocsvOutputSupport::new, supporters);
    }

    private @NonNull Csv.Format format = Csv.Format.DEFAULT;

    private @NonNull Csv.WriterOptions options = Csv.WriterOptions.DEFAULT;

    public @NonNull Csv.Writer newCsvWriter(@NonNull Path file) throws IOException {
        CharsetEncoder encoder = newEncoder(file);
        return Csv.Writer.of(getFormat(), getOptions(), newOutputStreamWriter(file, encoder), getCharBufferSize(file, encoder));
    }
}
