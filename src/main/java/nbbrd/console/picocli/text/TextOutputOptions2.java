package nbbrd.console.picocli.text;

import nbbrd.console.picocli.FileSink;
import picocli.CommandLine;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import static nbbrd.console.picocli.text.StdoutSink.DEFAULT_STDOUT_FILE;

@lombok.Getter
@lombok.Setter
public class TextOutputOptions2 implements TextOutput2 {

    @CommandLine.Option(
            names = {"--append"},
            description = "Append to the end of the output file.",
            defaultValue = "false"
    )
    private boolean append = false;

    private Charset encoding = Charset.forName(DEFAULT_ENCODING);

    private Path stdoutFile = Paths.get(DEFAULT_STDOUT_FILE);

    private StdoutSink stdoutSink = StdoutSink.getDefault();

    private FileSink fileSink = FileSink.getDefault();
}
