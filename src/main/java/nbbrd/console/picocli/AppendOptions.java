package nbbrd.console.picocli;

import picocli.CommandLine;

@lombok.Getter
@lombok.Setter
public class AppendOptions implements CommandSupporter<ByteOutputSupport> {

    @CommandLine.Option(
            names = {"--append"},
            description = "Append to the end of the output file.",
            defaultValue = "false"
    )
    private boolean append = false;

    @Override
    public void applyTo(ByteOutputSupport support) {
        support.setAppend(support.isAppend());
    }
}
