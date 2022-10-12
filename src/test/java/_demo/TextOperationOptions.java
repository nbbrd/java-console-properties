package _demo;

import picocli.CommandLine;

@lombok.Getter
@lombok.Setter
public class TextOperationOptions {

    @CommandLine.Option(
            names = "--operation",
            paramLabel = "<op>",
            description = "${DEFAULT-VALUE} / ${COMPLETION-CANDIDATES}",
            defaultValue = "NO_OP"
    )
    TextOperation operation;

    public String transform(String text) {
        return getOperation().apply(text);
    }
}
