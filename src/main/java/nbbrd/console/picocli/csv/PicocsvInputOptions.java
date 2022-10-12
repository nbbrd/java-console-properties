package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextInputOptions2;
import nbbrd.picocsv.Csv;

@lombok.Getter
@lombok.Setter
public class PicocsvInputOptions extends TextInputOptions2 implements PicocsvInput {

    private Csv.Format format = Csv.Format.DEFAULT;

    private Csv.ReaderOptions readerOptions = Csv.ReaderOptions.DEFAULT;
}
