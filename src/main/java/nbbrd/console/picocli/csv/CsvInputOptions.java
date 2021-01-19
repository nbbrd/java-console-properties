package nbbrd.console.picocli.csv;

import nbbrd.console.picocli.Profilable;
import nbbrd.console.picocli.text.TextInputOptions;
import nbbrd.picocsv.Csv;
import picocli.CommandLine;

@lombok.Data
public class CsvInputOptions extends TextInputOptions implements CsvInput, Profilable {

    @CommandLine.Option(
            names = {"-d", "--delimiter"},
            paramLabel = "<char>",
            description = "Delimiting character.",
            defaultValue = ","
    )
    private char delimiter;

    @CommandLine.Option(
            names = {"-n", "--new-line"},
            paramLabel = "<NewLine>",
            description = "NewLine type (${COMPLETION-CANDIDATES}).",
            defaultValue = "WINDOWS"
    )
    private Csv.NewLine separator;

    @CommandLine.Option(
            names = {"-l", "--lenient-line"},
            description = "Determine if NewLine is parsed leniently or not.",
            defaultValue = "false"
    )
    private boolean lenientSeparator;

    @CommandLine.Option(
            names = {"-q", "--quote"},
            paramLabel = "<char>",
            description = "Quoting character.",
            defaultValue = "\""
    )
    private char quote;
}
