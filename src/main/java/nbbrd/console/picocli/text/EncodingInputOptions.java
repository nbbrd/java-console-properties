package nbbrd.console.picocli.text;

import nbbrd.console.picocli.CommandSupporter;
import nbbrd.console.picocli.StandardCharsetCandidates;
import picocli.CommandLine;

import java.nio.charset.Charset;

import static nbbrd.console.picocli.text.CharsetSupplier.DEFAULT_ENCODING;

@lombok.Getter
@lombok.Setter
public class EncodingInputOptions implements CommandSupporter<TextInputSupport> {

    @CommandLine.Option(
            names = {"-e"},
            paramLabel = "<encoding>",
            description = "Charset used to encode text.",
            completionCandidates = StandardCharsetCandidates.class,
            defaultValue = DEFAULT_ENCODING
    )
    private Charset encoding = Charset.forName(DEFAULT_ENCODING);

    @Override
    public void applyTo(TextInputSupport support) {
        support.setFileEncoding(CharsetSupplier.of(encoding));
    }
}
