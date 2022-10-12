package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.text.TextOutputOptions2;
import nbbrd.picocsv.Csv;

@lombok.Getter
@lombok.Setter
public class PicocsvOutputOptions extends TextOutputOptions2 implements PicocsvOutput {

    private Csv.Format format = Csv.Format.DEFAULT;

    private Csv.WriterOptions writerOptions = Csv.WriterOptions.DEFAULT;
}
