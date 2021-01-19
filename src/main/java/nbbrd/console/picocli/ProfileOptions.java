package nbbrd.console.picocli;

import picocli.CommandLine;

@lombok.Data
public class ProfileOptions implements Profile {

    @CommandLine.Option(
            names = {"-P", "--profile"},
            hidden = true,
            defaultValue = ""
    )
    private String id;
}
