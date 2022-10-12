package nbbrd.console.picocli.text;

import nbbrd.console.picocli.FileSource;
import picocli.CommandLine;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import static nbbrd.console.picocli.text.StdinSource.DEFAULT_STDIN_FILE;

@lombok.Getter
@lombok.Setter
public class TextInputOptions2 implements TextInput2 {

    @CommandLine.Option(names = {"--dummyDefaultTextInput2"}, hidden = true)
    private boolean dummyDefaultTextInput2 = false;

    private Charset encoding = Charset.forName(DEFAULT_ENCODING);

    private Path stdinFile = Paths.get(DEFAULT_STDIN_FILE);

    private StdinSource stdinSource = StdinSource.getDefault();

    private FileSource fileSource = FileSource.getDefault();
}
