package nbbrd.console.picocli.text;

import nbbrd.console.picocli.CommandSupporter;
import nbbrd.console.picocli.StandardCharsetCandidates;
import picocli.CommandLine;

import java.nio.charset.Charset;

import static nbbrd.console.picocli.text.CharsetSupplier.DEFAULT_ENCODING;

@lombok.Getter
@lombok.Setter
public class EncodingOutputOptions implements CommandSupporter<TextOutputSupport> {

    @CommandLine.Option(
            names = {"-E"},
            paramLabel = "<encoding>",
            description = "Charset used to encode text.",
            completionCandidates = StandardCharsetCandidates.class,
            defaultValue = DEFAULT_ENCODING
    )
    private Charset encoding = Charset.forName(DEFAULT_ENCODING);

    @Override
    public void applyTo(TextOutputSupport support) {
        support.setFileEncoding(CharsetSupplier.of(encoding));
    }
}
